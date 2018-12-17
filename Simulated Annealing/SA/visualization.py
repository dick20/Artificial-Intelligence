import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import math
import copy
import imageio
import os
import os.path


CITY_COUNT = 131

def read_tsp_data(filePath):
    city = pd.read_table(filePath, sep = '\n', header = None)
    city.columns = ['x']
    city['y'] = None

    for i in range(len(city)):
        Str = city['x'][i].split()
        city['x'][i] = float(Str[1])
        city['y'][i] = float(Str[2])
    return city


def read_tsp_path(filePath):
    paths = []
    pathData = pd.read_table(filePath, sep = '\n', header = None)
    pathData.columns = ['x']

    count = 0
    j = 0
    while j < len(pathData):
        path = []
        for i in range(j,j+CITY_COUNT):
            Str = pathData['x'][i].split()
            path.append(int(Str[1]) - 1)
            count += 1
        paths.append(path)
        j += CITY_COUNT
    return paths

def read_tsp_optimum(filePath):
    pathData = pd.read_table(filePath, sep = '\n', header = None)
    pathData.columns = ['x']
    path = []
    i = 0
    while i < len(pathData):
        Str = pathData['x'][i]
        path.append(float(Str))
        i += 1
    return path

# Create the fig.
def create_figure_tsp(citys, paths):
    for j in range(len(paths)):
        path = paths[j]

        #print(path)
        x = []
        y = []
        for i in range(len(citys)):
            x.append(citys['x'][path[i]])
            y.append(citys['y'][path[i]])
        x.append(citys['x'][path[0]])
        y.append(citys['y'][path[0]])
        citys['order'] = path
        citys_order = citys.sort_values(by=['order'])
        plt.plot(x, y, linewidth = 1.0)
        plt.scatter(x, y, s = 10, color = 'g', marker = ',')
        plt.savefig("./image/"+str(j)+".png")
        plt.clf()
        #plt.show()

def create_figure_optimum(optimum):
    x = []
    y = []
    for i in range(len(optimum)):
        x.append(optimum[i])
        y.append(i)
    plt.plot(y, x, linewidth = 1.0)
    plt.xlabel("Annealing times") #X轴标签
    plt.ylabel("Current global optimal") #Y轴标签
    plt.title("TSP simulated annealing process") #标题
    plt.show()

def create_gif(gif_name, path, duration = 0.3):
    '''
    生成gif文件，原始图片仅支持png格式
    gif_name ： 字符串，所生成的 gif 文件名，带 .gif 后缀
    path :      需要合成为 gif 的图片所在路径
    duration :  gif 图像时间间隔
    '''
    frames = []
    pngFiles = os.listdir(path)
    image_list = [os.path.join(path, f) for f in pngFiles]
    count = 0
    for j in range(291):
        # 读取 png 图像文件
        frames.append(imageio.imread("./image/"+str(j)+".png"))
        count += 1
    # 保存为 gif 
    print(str(count))
    imageio.mimsave(gif_name, frames, 'GIF', duration = duration)

    return


if __name__ == '__main__':
    citys = read_tsp_data("tspdata.txt")
    paths  = read_tsp_path("result.txt")
    optimum = read_tsp_optimum("result2.txt")
    create_figure_optimum(optimum)
    #create_figure_tsp(citys, paths)
    gif_name = 'created_gif.gif'
    path = './image'   #指定文件路径
    duration = 0.3
    create_gif(gif_name, path, duration)