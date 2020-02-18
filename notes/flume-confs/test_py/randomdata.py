# -*- coding: utf-8 -*-
"""
Created on Fri Jan 31 19:48:01 2020

@author: Administrator
"""

import time
import random
import numpy as np

def to_txt(data):
    str_data = str(data)
    with open("random_data.txt", "a") as f:
        f.write(str_data + '\n')

print(random.randrange(10, 100, 3))

flag = 1
while flag:
    data = np.round(np.random.rand(1,10) * 1000,0)
    print('---->', flag)
    
    to_txt(data)
    
    flag += 1
    if flag >= 1000:
        flag = 0
    
    time.sleep(0.5)



    
