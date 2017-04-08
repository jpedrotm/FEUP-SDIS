#!/usr/bin/env python
import os
import subprocess
from sys import argv, exit
from itertools import izip_longest
import subprocess


def grouper(n, iterable, fillvalue=None):
    args = [iter(iterable)] * n
    return izip_longest(fillvalue=fillvalue, *args)


full_path = os.path.realpath(__file__)
path, _ = os.path.split(full_path)
os.chdir(path)


if len(argv) < 9:
    help = """ 
    Arguments: run_servers.py <Version> <MC Address> <MC Port> <MDB Address> <MDB Port> <MDR Address> <MDR Port> [<Server ID> <Service Access Point>]+
    """
    print(help)
    exit()


version = argv[1]
channels = " ".join(argv[2:8])


# Start RMI
p1 = subprocess.Popen(['ps', '-ef'], stdout=subprocess.PIPE)
p2 = subprocess.Popen(['grep', 'rmiregistry'], stdin=p1.stdout, stdout=subprocess.PIPE)
p3 = subprocess.Popen(['awk', "{ print $2 }"], stdin=p2.stdout, stdout=subprocess.PIPE)
output, err = p3.communicate()

entries = output.count('\n')

if (entries <= 1):
    os.chdir('bin/')
    rmi_process = subprocess.Popen('rmiregistry')
    os.chdir('..')


# Start servers
for item1, item2 in grouper(2, argv[8::]):
    cmd = " ".join(['java -cp bin/ server.Server', item1, channels])
    process = subprocess.Popen(['gnome-terminal', '-e', cmd], stdout=subprocess.PIPE)
    output, error = process.communicate();

# Close running RMIs
# ps -ef | grep rmiregistry | awk '{ print $2 }' | xargs kill