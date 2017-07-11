import glob
import os
import shutil

def main(dest_root):
    """get file from neuro_morpho_atlas"""
    print("Start...")
    for filename in glob.iglob('neuro_morphometric_atlas/**/*.nii', recursive=True):
        print("Copy file " + filename + " in IRM_Experience")
        shutil.copy(filename, dest_root)

if __name__ == '__main__':
    main("IRM_Experience/")
