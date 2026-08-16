"""
A test harness to collect data from both FirelineParallel and FirelineSerial Java files

Author: Tauriq Petersen 
Date started: 15/08/2026
"""

import numpy as np

import subprocess
from pathlib import Path 

script_dir = Path(__file__).parent
proj_dir = script_dir

file_path_input = script_dir/"InputData.txt"
file_path_ParallelData = script_dir/"ParallelData.txt"
Seq_cutoff = 50
Array = []

print('Starting script')
print('Hold on a bit, this may take a while.....')

with open(file_path_input,"r", encoding="utf-8") as file:
    while True:
        line = file.readline().strip()
        Array.append(line)


        if line:
            Array.append(line)

        if not line:
            break

for i in range(1):
    timings = []
    for j in range(5):
        new_Cutoff = Seq_cutoff + 25*j
        ARGS_String = Array[i] + " " +str(new_Cutoff)

        if i != 0 or i != 1 :
            result = subprocess.run(f'make -C "{proj_dir}" run-parallel ARGS="{ARGS_String}"', shell=True, capture_output=True, text=True, check=True)
            output = result.stdout

            for line in output.splitlines():
                if "Core simulation time:" in line:
                    timings.append(line.split().pop(3))

        else:
            subprocess.run(f'make -C "{proj_dir}" run-parallel ARGS="{ARGS_String}"', shell=True, capture_output=True, text=True, check=True)


    StrBuild =  f"{i-1:<25}" + "".join(f"{timing:<35}" for timing in timings)

    with open(file_path_ParallelData, "a") as file:
        file.write(StrBuild + "\n")




