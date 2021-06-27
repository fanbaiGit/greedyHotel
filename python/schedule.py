import json

from flask import Flask, redirect, url_for
from flask_restful import Api, Resource, reqparse, request
from flask import jsonify
import threading
from room_class import client
from readerwriterlock import rwlock
import time
import sys

y = 3
app = Flask(__name__)
api = Api(app)
client_dict = dict()
marker = rwlock.RWLockFair()

interval = 5

df = {'1': 32, '2': 28, '3': 30, '4': 29}


def schedule_clients2():
    while (True):
        print(
            "-------------------------------------------------------------------------"
        )
        time.sleep(60)
        #等待数目
        wait_cnt = 0

        #获得调度数目
        busy_cnt = 0

        #等待队列
        waitlist = []

        #调度队列
        busylist = []

        #读写锁
        read_marker = marker.gen_rlock()
        write_maker = marker.gen_wlock()

        #先更新一遍状态
        read_marker.acquire()

        for room_id in client_dict:
            if client_dict[room_id].busy:
                busy_cnt += 1
                #风速，已运行时间，id
                busylist.append((client_dict[room_id].get_wind_speed(),
                                 client_dict[room_id].get_time(), room_id))
            elif client_dict[room_id].is_runable:  #可运行但是没获得调度
                wait_cnt += 1
                #风速，已等待时间，id
                waitlist.append((client_dict[room_id].get_wind_speed(),
                                 client_dict[room_id].get_time(), room_id))
            client_dict[room_id].update_info()
        print(busylist, waitlist)
        read_marker.release()

        #当前调度剩余能力
        free_cnt = y - busy_cnt

        print("waiting rooms number: ", wait_cnt)
        print("running rooms number: ", busy_cnt)
        print("service left: ", free_cnt)
        for room_id in range(len(waitlist)):
            print("waiting room_id: ", waitlist[room_id][2])
        for room_id in range(len(busylist)):
            print("running room_id: ", busylist[room_id][2])
        #如果有空闲的，直接调度
        if free_cnt > 0:
            waitlist = sorted(waitlist,
                              key=lambda waitlist:
                              (-waitlist[0], -waitlist[1]))  #按优先级降序，时间降序

            for _ in range(len(waitlist)):
                #如果没有送风能力了就break，不需要再检查了
                if free_cnt <= 0:
                    break
                #先是优先级调度
                write_maker.acquire()
                client_dict[waitlist[0][2]].run()
                write_maker.release()
                print(waitlist[0][2], " has got the air service")
                waitlist.pop(0)
                free_cnt -= 1

        # 现在等待队列要么空了，要么free_cnt <= 0了
        # 如果没空且 free_cnt 小于0，那就进行抢占，先按优先级
        if free_cnt <= 0 and len(waitlist) > 0:
            busylist = sorted(
                busylist, key=lambda busylist:
                (busylist[0], -busylist[1]))  #等待替换的队列先按优先级升序，再时间降序
            #print(busylist)

            write_maker.acquire()
            while (True):
                #如果等待队列还有剩余      可能被替换的队列还有剩余       第一个可以被替换的房间优先级比第一个可以抢占他人的房间优先级低
                if len(waitlist) > 0 and len(
                        busylist) > 0 and busylist[0][0] < waitlist[0][0]:
                    client_dict[busylist[0][2]].wait()  # 运行队列的最低优先级的等待
                    client_dict[waitlist[0][2]].run()  # 等待队列的最高优先级运行
                    print("priority schedule: ", busylist[0][2],
                          "'s service has been occupied by ", waitlist[0][2])
                    waitlist.pop(0)
                    busylist.pop(0)
                # 如果等待队列还有剩余      可能被替换的队列还有剩余       第一个可以被替换的房间优先级和第一个可以抢占他人的房间优先级一样
                elif len(waitlist) > 0 and len(
                        busylist) > 0 and busylist[0][0] == waitlist[0][0]:
                    client_dict[busylist[0][2]].wait()  # 运行队列的最低优先级的等待
                    client_dict[waitlist[0][2]].run()  # 等待队列的最高优先级运行
                    print("run time schedule: ", busylist[0][2],
                          "'s service has been occupied by ", waitlist[0][2])
                    waitlist.pop(0)
                    busylist.pop(0)
                else:
                    break
            write_maker.release()


def schedule_clients():

    while (True):
        print(
            "-------------------------------------------------------------------------"
        )
        time.sleep(interval)
        #等待数目
        wait_cnt = 0

        #获得调度数目
        busy_cnt = 0

        #等待队列
        waitlist = []

        #调度队列
        busylist = []

        #读写锁
        read_marker = marker.gen_rlock()
        write_maker = marker.gen_wlock()

        #先更新一遍状态
        read_marker.acquire()

        for room_id in client_dict:
            if client_dict[room_id].busy:
                busy_cnt += 1
                #风速，已运行时间，id
                busylist.append((client_dict[room_id].get_wind_speed(),
                                 client_dict[room_id].get_time(), room_id))
            elif client_dict[room_id].is_runable:  #可运行但是没获得调度
                wait_cnt += 1
                #风速，已等待时间，id
                waitlist.append((client_dict[room_id].get_wind_speed(),
                                 client_dict[room_id].get_time(), room_id))
            client_dict[room_id].update_info()
        print(busylist, waitlist)
        read_marker.release()

        #当前调度剩余能力
        free_cnt = y - busy_cnt

        print("waiting rooms number: ", wait_cnt)
        print("running rooms number: ", busy_cnt)
        print("service left: ", free_cnt)
        for room_id in range(len(waitlist)):
            print("waiting room_id: ", waitlist[room_id][2])
        for room_id in range(len(busylist)):
            print("running room_id: ", busylist[room_id][2])
        #如果有空闲的，直接调度
        if free_cnt > 0:
            waitlist = sorted(waitlist,
                              key=lambda waitlist:
                              (-waitlist[0], -waitlist[1]))  #按优先级降序，时间降序

            for _ in range(len(waitlist)):
                #如果没有送风能力了就break，不需要再检查了
                if free_cnt <= 0:
                    break
                #先是优先级调度
                write_maker.acquire()
                client_dict[waitlist[0][2]].run()
                write_maker.release()
                print(waitlist[0][2], " has got the air service")
                waitlist.pop(0)
                free_cnt -= 1

        # 现在等待队列要么空了，要么free_cnt <= 0了
        # 如果没空且 free_cnt 小于0，那就进行抢占，先按优先级
        if free_cnt <= 0 and len(waitlist) > 0:
            busylist = sorted(
                busylist, key=lambda busylist:
                (busylist[0], -busylist[1]))  #等待替换的队列先按优先级升序，再时间降序
            #print(busylist)

            write_maker.acquire()
            while (True):
                #如果等待队列还有剩余      可能被替换的队列还有剩余       第一个可以被替换的房间优先级比第一个可以抢占他人的房间优先级低
                if len(waitlist) > 0 and len(
                        busylist) > 0 and busylist[0][0] < waitlist[0][0]:
                    client_dict[busylist[0][2]].wait()  # 运行队列的最低优先级的等待
                    client_dict[waitlist[0][2]].run()  # 等待队列的最高优先级运行
                    print("priority schedule: ", busylist[0][2],
                          "'s service has been occupied by ", waitlist[0][2])
                    waitlist.pop(0)
                    busylist.pop(0)
                # 如果等待队列还有剩余      可能被替换的队列还有剩余       第一个可以被替换的房间优先级和第一个可以抢占他人的房间优先级一样
                elif len(waitlist) > 0 and len(
                        busylist) > 0 and busylist[0][0] == waitlist[0][0]:
                    client_dict[busylist[0][2]].wait()  # 运行队列的最低优先级的等待
                    client_dict[waitlist[0][2]].run()  # 等待队列的最高优先级运行
                    print("run time schedule: ", busylist[0][2],
                          "'s service has been occupied by ", waitlist[0][2])
                    waitlist.pop(0)
                    busylist.pop(0)
                else:
                    break
            write_maker.release()


@app.route("/shutdown", methods=["POST"])
def shutdown():
    room_id = str(request.get_json())
    if room_id in client_dict:
        write_maker = marker.gen_wlock()
        write_maker.acquire()
        del client_dict[room_id]
        write_maker.release()
        return 'shutdown ok'
    else:
        return 'ohh'


@app.route("/syc", methods=["POST"])
def synchronize():
    room_id = str(request.get_json())
    state = 0
    if room_id in client_dict:
        if client_dict[room_id].busy:
            if client_dict[room_id].mode == "HOT":
                state = 2
            elif client_dict[room_id].mode == "COLD":
                state = 1
        else:
            state = 0

        msg_json = {
            "isDisabled": "False",
            "nowTemp": client_dict[room_id].now_tp,
            "roomId": client_dict[room_id].id,
            "state": state,
            "tarTemp": client_dict[room_id].target_tp,
            "windSpeed": client_dict[room_id].wind_speed,
        }
        return json.dumps(msg_json, ensure_ascii=False)
    else:
        return 'ohh'


@app.route("/op", methods=["POST"])
def get_op_request():
    # room_id = str(request.headers["room_id"])
    # mode = int(request.headers["state"])
    # target_tp = int(request.headers["tar_temp"])
    # wind_speed = str(request.headers["wind_speed"])

    msg = request.get_json()
    room_id = str(msg['roomId'])  #房间id
    wind_speed = str(msg['windSpeed'])
    target_tp = int(msg['tarTemp'])
    mode = int(msg['state'])
    now_tp = int(msg['nowTemp'])
    print(msg)

    if mode == 0 and room_id in client_dict:
        write_maker = marker.gen_wlock()
        write_maker.acquire()
        del client_dict[room_id]
        write_maker.release()
        return "shutdown ok"

    if target_tp <= now_tp:
        mode = "COLD"
    elif target_tp > now_tp:
        mode = "HOT"

    # 如果是第一次发请求的房间，就给一个线程
    write_maker = marker.gen_wlock()
    write_maker.acquire()
    if room_id not in client_dict:
        print("room_id:", room_id, " joins in, the wind speed is set as ",
              wind_speed)
        dftmp = df[room_id]
        newclient = client(room_id, wind_speed, target_tp, mode, dftmp, now_tp)
        client_dict[room_id] = newclient  #新来的房间上来默认先等待
    else:
        print("room_id:", room_id, " sets wind speed as ", wind_speed,
              ", target_tp as ", target_tp, "mode as ", mode)
        client_dict[room_id].update_info(wind_speed, target_tp, mode)

    write_maker.release()
    return "op ok"


if __name__ == '__main__':
    #y = int(input("y is:"))
    raw_arguments = sys.argv[1]
    y = raw_arguments['y']
    fresher = threading.Thread(target=schedule_clients)
    fresher.start()
    app.run()
