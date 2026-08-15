"""
A test harness and data collection that will 

Author: Tauriq Petersen 
Date started: 15/08/2026
"""

import numpy as np

with open("InputData.txt","r", encoding="utf-8") as file:
    while True:
        line = file.readline()


        if not line:
            break

        print(line)



