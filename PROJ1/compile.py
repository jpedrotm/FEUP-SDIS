#!/usr/bin/env python
import os
import subprocess

full_path = os.path.realpath(__file__)
path, _ = os.path.split(full_path)

os.chdir(path)
os.system('javac -d bin src/*/*.java')

#os.system("gnome-terminal -e 'bash -c \"javac -d bin src/*/*.java; exec bash\"'")
