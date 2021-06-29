import time


class client():
    def __init__(self, room_id, wind_speed, target_tp, mode, now_tp, dftmp):
        self.id = room_id
        self.wind_speed = wind_speed
        self.busy = False
        self.started_time = time.time()
        self.waited_time = time.time()
        self.target_tp = target_tp
        self.now_tp = now_tp
        self.is_runable = True
        self.mode = mode
        if dftmp > target_tp:
            self.action = "DOWN"
        else:
            self.action = "UP"
        self.sec = 60
        self.interval = 1
        self.defaulttemp = dftmp

    def run(self):
        #接受调度
        #开始运行的时间变成当前时间
        self.busy = True
        self.started_time = time.time()
        print(self.id, "start to run")

    def wait(self):
        print(self.id, "start to wait")
        self.busy = False
        self.waited_time = time.time()

    def get_wind_speed(self):
        if self.wind_speed == "HIGH":
            return 3
        if self.wind_speed == "MIDDLE":
            return 2
        if self.wind_speed == "LOW":
            return 1
        else:
            return "here's an error"

    def update_info(self, wind_speed=None, tar_temp=None, mode=None):

        if self.busy:  #如果忙，更新温度和计费标准

            #先更新温度
            #delta是温度变化率
            delta = 0
            if self.wind_speed == "HIGH":  # 高风
                delta = 0.6
            if self.wind_speed == "MIDDLE":  # 中风
                delta = 0.5
            if self.wind_speed == "LOW":
                delta = 0.4

            if self.target_tp < self.now_tp:
                self.action = "DOWN"
                self.now_tp -= delta / self.sec * self.interval  # 现在的温度
                #print("update temp line60")
            elif self.target_tp > self.now_tp:
                self.action = "UP"
                self.now_tp += delta / self.sec * self.interval
                #print("update temp line63")
            else:
                print(self.id, " is at its target temperatrue")

            print(self.id, "'s current temperature is", self.now_tp)
            print(self.id, "'s target temperature is", self.target_tp)

            #到达目标温度，停机
            if self.action == "UP" and self.now_tp >= self.target_tp:
                print(self.id,
                      "'s temperature is higher than its target, stops")
                self.is_runable = False
                self.wait()
            elif self.action == "DOWN" and self.target_tp >= self.now_tp:
                print(self.id,
                      "'s temperature is lower than its target, stops")
                self.is_runable = False
                self.wait()
            #总执行时间不要改！

        else:  #把温度自然地往默认度靠拢
            if self.defaulttemp < self.now_tp:
                self.action = "DOWN"
                self.now_tp -= 0.5 / self.sec * self.interval  # 现在的温度
                #print("update temp line87")
            elif self.defaulttemp > self.now_tp:
                self.action = "UP"
                self.now_tp += 0.5 / self.sec * self.interval
                #print("update temp line90")
            else:
                pass

            print(self.id, "'s temperature is ", self.now_tp)
            print(self.id, "'s target is ", self.target_tp)

            #超过目标温度一度，允许被调度
            if (self.action == "UP" and self.now_tp - self.target_tp >= 1) or (
                    self.action == "DOWN"
                    and self.target_tp - self.now_tp >= 1):
                self.is_runable = True
                print(self.id, " is allowed to be scheduled")

        #结算完当前状态后，再修改属性
        if wind_speed is not None:
            self.wind_speed = wind_speed
            self.is_runable = True
        if tar_temp is not None:
            self.target_tp = tar_temp
            self.is_runable = True
        if mode is not None:
            self.mode = mode

    def get_time(self):
        #查询当前房间等待的时间
        if self.busy:
            return time.time() - self.started_time
        else:
            return time.time() - self.waited_time
