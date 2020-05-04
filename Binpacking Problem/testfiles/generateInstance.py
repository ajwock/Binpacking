import sys
import random

def main():
  with (open(sys.argv[1], 'w')) as file:
    numberOfItems = int(sys.argv[3])
    capacityRatio = float(sys.argv[2])
    capacity = int((capacityRatio / 100) * numberOfItems)
    file.write("k " + str(numberOfItems) + " " + str(capacity) + "\n")
    for i in range(0, numberOfItems):
	    file.write(str(i) + " " + str(int(random.uniform(1, capacity))) + "\n")

main()
