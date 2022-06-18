import json
import time

from locust import TaskSet, task, HttpUser

import string
import random  # define the random module

login_headers = {"Accept": "application/json", "Content-Type": "application/x-www-form-urlencoded"}
initialized = False
supervisor_token = ""


def post_headers(token):
    return {
        "Accept": "application/json, text/plain, */*",
        "Content-Type": "application/json;charset=utf-8",
        "Authorization": "Bearer " + token
    }


def get_headers(token):
    return {
        'Authorization': "Bearer " + token,
        'access-control-allow-origin': "*"
    }


class UserTaskSet(TaskSet):

    def on_start(self):
        global initialized
        global supervisor_token

        if not initialized:
            login_response = self.client.post("/user/login", data="username=ronit&password=1234abcd",
                                              headers=login_headers)
            supervisor_token = login_response.json()["access_token"]
            initialized = True

        self.username = ''.join(random.choices(string.ascii_uppercase + string.digits, k=10))

        self.client.post("/user/registerUser",
                         data=json.dumps({
                             "workField": "", "userToRegister": self.username,
                             "password": "1234abcd",
                             "userStateEnum": "INSTRUCTOR", "firstName": "n",
                             "lastName": "l",
                             "email": "", "phoneNumber": "", "city": "city", "schools": []
                         }),
                         headers=post_headers(supervisor_token))

        time.sleep(1)
        login_response = self.client.post("/user/login", data="username=" + self.username + "&password=1234abcd",
                                          headers=login_headers)
        self.access_token = login_response.json()["access_token"]

    def on_stop(self):
        self.client.post("/user/logout", headers=post_headers(self.access_token))

    @task(2)
    def view_schools(self):
        self.client.get("/data/getUserSchools", headers=get_headers(self.access_token))

    @task(3)
    def view_workplan(self):
        self.client.get("/user/viewWorkPlan/year=2022&month=10",
                        headers=get_headers(self.access_token))

    @task(2)
    def edit_info(self):
        self.client.post("/user/updateInfo", data=json.dumps({
            "firstName": "a",
            "lastName": "a",
            "email": "a@gmail.com",
            "phoneNumber": "055-555-5555",
            "city": "a"
        }), headers=post_headers(self.access_token))

    @task(1)
    def assign_school(self):
        letters = string.digits
        school_symbol = ''.join(random.choice(letters) for _ in range(10))
        self.client.post("/data/insertSchool",
                         data=json.dumps({
                             "symbol": school_symbol,
                             "name": "testing school" + school_symbol, "city": "", "city_mail": "",
                             "address": "", "school_address": "", "principal": "", "manager": "",
                             "supervisor": "", "phone": "", "mail": "", "zipcode": 1010,
                             "education_stage": "", "education_type": "",
                             "supervisor_type": "", "spector": "", "num_of_students": 30
                         }),
                         headers=post_headers(supervisor_token))
        self.client.post("/user/assignSchoolToUser",
                         data=json.dumps({"affectedUser": self.username, "school": school_symbol}),
                         headers=post_headers(supervisor_token))


class UserLocust(HttpUser):
    tasks = [UserTaskSet]
