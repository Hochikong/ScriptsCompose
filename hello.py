import argparse
import time
from datetime import datetime

if __name__ == '__main__':
    parser = argparse.ArgumentParser("My test script")
    parser.add_argument('-n', '--name', type=str, help="name")

    args = parser.parse_args()
    
    print(f"Hello {args.name}")