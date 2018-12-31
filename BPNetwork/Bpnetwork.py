# -*- coding: utf-8 -*-
import math
import random
import numpy as np
import scipy.io as sio
import pandas as pd
import struct
import os
from PIL import Image

# Load mnist file method refers to https://www.cnblogs.com/xianhan/p/9145966.html
def load_mnist(path, kind='train'):
    labels_path = os.path.join(path, '%s-labels.idx1-ubyte' % kind)
    images_path = os.path.join(path, '%s-images.idx3-ubyte' % kind)
    with open(labels_path, 'rb') as lbpath:
        magic, n = struct.unpack('>II',
                          lbpath.read(8))
        labels = np.fromfile(lbpath, dtype=np.uint8)

    with open(images_path, 'rb') as imgpath:
        magic, num, rows, cols = struct.unpack('>IIII',
                                        imgpath.read(16))
        images = np.fromfile(imgpath, dtype=np.uint8).reshape(len(labels), 784)

    return images, labels

# Test the digit in the A4 paper.
def readTestImage(path):
    # 1. Convert the image into grey image.
    image = Image.open(path).convert('L')
    width, height = image.size
    pixel = image.load()
    # image.show()
    
    for i in range(width):
        for j in range(height):
            if pixel[i, j] >= 100:
                pixel[i, j] = 0
            else:
                pixel[i, j] = 255 - pixel[i, j]

    # 2. Resize the image into 28 * 28.
    if image.size[0] != 28 or image.size[1] != 28:
        image = image.resize((28, 28))

    # 3. Normalize the image.
    arr = []
    for i in range(28):
        for j in range(28):
            arr.append(float(image.getpixel((j, i))) / 255.0)

    # 4. Turn into a 784 dim vector.
    # digitVector = np.array(arr).reshape(1, 784)
    # print(digitVector)

    # Show the image after resize.
    # image.show()

    return arr


class BPNetwork:


    def __init__(self, _stdRate, _optNum):
        print("正在初始化神经网络")


        [self.sample, self.label] = load_mnist("./mnist")
        self.sample = self.sample.astype('float')
        self.label = self.label.astype('int32')
        self.sample /= 256.0


        # Define the number of each layer.
        self.sampleNum = len(self.sample)
        self.iptNum = len(self.sample[0])
        self.optNum = _optNum
        self.hidNum = (int)(math.sqrt(self.iptNum + self.optNum) + random.randint(0, 9))
        
        self.hidOffset = np.zeros(self.hidNum)
        self.optOffset = np.zeros(self.optNum)

        self.w1 = 0.2 * np.random.random((self.iptNum, self.hidNum)) - 0.1
        self.w2 = 0.2 * np.random.random((self.hidNum, self.optNum)) - 0.1

        self.iptStudyRate = _stdRate
        self.hidStudyRate = _stdRate

        print("训练次数: {:d}".format(self.sampleNum))
        # print(self.sampleNum)
        print("学习率: {:.4f}".format(_stdRate))
        # print(_stdRate)
        print("隐层节点的个数: {:d}".format(self.hidNum))
        # print(self.hidNum)

        self.NetworkTrain()
        self.NetworkTest()
        self.selfTest(3, "./mnist/3.bmp")
        self.selfTest(5, "./mnist/5.bmp")
        self.selfTest(7, "./mnist/7.bmp")

    def Sigmoid(self, x):
        inputValue = []
        for i in x:
            inputValue.append(1/(1+math.exp(-i)))
        inputValue = np.array(inputValue)
        return inputValue

    def NetworkTrain(self):
        print("正在训练神经网络")
        for cc in range(0, self.sampleNum):
            trainLabel = np.zeros(self.optNum)
            trainLabel[self.label[cc]] = 1

            # Forward translate.
            # Calculate the vector of the hidden.
            hidVector = np.dot(self.sample[cc], self.w1) + self.hidOffset
            hidActive = self.Sigmoid(hidVector)
            # print(hidActive)

            # Calculate the vecotr of the output.
            optVector = np.dot(hidActive, self.w2) + self.optOffset
            optActive = self.Sigmoid(optVector)

            # Backward Translate.
            e = trainLabel - optActive
            optDelta = e * optActive * (1-optActive)
            hidDelta = hidActive * (1-hidActive) * np.dot(self.w2, optDelta)

            # Update weight.
            for i in range(0, self.optNum):
                self.w2[:,i] += self.hidStudyRate * optDelta[i] * hidActive
            for i in range(0, self.hidNum):
                self.w1[:,i] += self.iptStudyRate * hidDelta[i] * self.sample[cc]
            
            self.optOffset += self.hidStudyRate * optDelta
            self.hidOffset += self.iptStudyRate * hidDelta

    def NetworkTest(self):
        print("正在测试神经网络")
        [test, testLabel] = load_mnist("./mnist", kind = "t10k")
        test = test.astype("float")
        testLabel = testLabel.astype("int32")
        #test = sio.loadmat(dataFileName)
        #test_s = test["mnist_test"]
        test_s = test / 256.0

        #testLabel = sio.loadmat(labelFileName)
        #test_l = testLabel["mnist_test_labels"]
        test_l = testLabel

        digitTestResult = np.zeros(10)
        digitNumber = np.zeros(10)

        for i in test_l:
            digitNumber[i] += 1

        for cc in range(len(test_s)):
            hidVector = np.dot(test_s[cc], self.w1) + self.hidOffset
            hidActive = self.Sigmoid(hidVector)

            optVector = np.dot(hidActive, self.w2) + self.optOffset
            optActive = self.Sigmoid(optVector)

            print("标签数字为: {:d} 预测数字为: {:d}".format(test_l[cc], np.argmax(optActive)))

            # if the output of the digit equals to the right number, add one.
            if np.argmax(optActive) == test_l[cc]:
                digitTestResult[test_l[cc]] += 1

        # print the result statistic.
        print("原始测试数据中，各数字的分布如下所示:")
        print(digitNumber)
        print("通过训练模型得到的各数字的分布如下所示:")
        print(digitTestResult)

        # print the correct rate.
        correct_rate = digitTestResult / digitNumber
        # print(correct_rate)
        correct_rate = digitTestResult.sum() / len(test_s)
        print("准确率为: {:.4f}".format(correct_rate))
        # print(correct_rate)

    def selfTest(self, dig, path):
        digit = readTestImage(path)
        hidDigit = np.dot(digit, self.w1) + self.hidOffset
        hid = self.Sigmoid(hidDigit)
        oo = np.dot(hid, self.w2) + self.optOffset
        op = self.Sigmoid(oo)
        print("测试数字为: {:d} 预测数字为: {:d}".format(dig, np.argmax(op)))


if __name__ == '__main__':
    bpNetwork = BPNetwork(0.35, 10)