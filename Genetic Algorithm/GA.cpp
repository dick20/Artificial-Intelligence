#include <iostream>  
#include <string.h>  
#include <fstream>  
#include <iterator>  
#include <algorithm>  
#include <limits.h>  
#include <math.h>  
#include <stdlib.h>  
#include <ctime>
#include <vector>
#include <map>

using namespace std;

const int nCities = 131; 					// 城市数量  
const int BEST = 564;						// 最优解

double length_table[nCities][nCities];		// 存储各点间距离
	
const int group_size = 1000;					// 种群大小
const int time_to_breed = 8000;				// 繁殖代数
const double p_cross = 0.8;					// 交叉概率
double p_variation = 0.5;					// 变异概率

// 一个个体
class Path 
{
public:
	// 计算路径的长度
	void Calculate_length() {
		length = 0;
		//遍历path
		for (int i = 1; i < nCities; i++) {
			length += length_table[path[i - 1] - 1][path[i] - 1];
		}
		length += length_table[path[nCities - 1] - 1][path[0] - 1];
	}

	// 随机生成一个解
	Path() {
		length = 0;
		generate_random();
		Calculate_length();
	}

	void generate_random(){
		for (int i = 0; i < nCities; ++i)
		{
			path[i] = i+1;
		}
		srand(time(0));
		/*for (int i = 0; i < nCities; ++i)
		{
			int city1 = rand() % nCities;
			int city2 = rand() % nCities;
			int temp = path[city2];
			path[city2] = path[city1];
			path[city1] = temp;
		}*/
	}

	void getNewSolution_cross(Path &t) {
		// 单点交叉
		int mark = rand() % (nCities - 2) + 1;//1 to nCities - 2
		for (int i = mark; i < nCities; i++) {
			int temp = path[i];
			path[i] = t.path[i];
			t.path[i] = temp;
		}
		// 判断解的合法性
		int i = 0; int j = 0;
		bool count_dup_1[nCities + 1] = { false };
		bool count_dup_2[nCities + 1] = { false };
		while (i < nCities && j < nCities) {
			if (count_dup_1[path[i]] && count_dup_2[t.path[j]]) {
				int temp = path[i];
				path[i] = t.path[j];
				t.path[j] = temp;
				i++;
				j++;
			}
			if (i >= nCities || j >= nCities)
				break;
			if (!count_dup_1[path[i]]) {
				count_dup_1[path[i]] = true;
				i++;
			}
			if (!count_dup_2[t.path[j]]) {
				count_dup_2[t.path[j]] = true;
				j++;
			}
		}
		Calculate_length();
		t.Calculate_length();
	}

	// 三种变异操作,在组内变化，不会出现非法解
	void getNewSolution_variation() {
		int i = rand() % nCities;
		int j = rand() % nCities;
		if (i > j) {
			swap(i, j);
		}
		else if (i == j)return;
		//随机取路径中两点进行操作

		int choose = rand() % 3;
		switch (choose) {
		case 0:
			swap(path[i], path[j]); break;
		case 1:
			reverse(path + i, path + j); break;
		default:
			if (j == (nCities - 1)) return;
			rotate(path + i, path + j, path + j + 1);
		}
		Calculate_length();
	}

	double getLength(){
		return length;
	}

	int* getPath(){
		return path;
	}

private:
	double length;//代价，总长度
	int path[nCities];//路径
};

class GA{
public:
	struct node{
		int num;
		double x;
		double y;
	}nodes[nCities];

	//种群，大小为group_size
	vector<Path> group;

	void init_dis();
	
	GA();
	
	~GA();

	Path getPath();

private:
	void choose(vector<Path>& group);
	
	void cross(vector<Path>& group);

	void variation(vector<Path>& group);

	void judge(vector<Path> & old_group, vector<Path> & group);

	void init();
};

GA::GA(){
}

GA::~GA(){
	group.clear();
}

void GA::init(){
	// 初始化种群
	group.resize(group_size,Path());	
	// 给种群一些 局部搜索出来的最优解
	Path new_path;
	Path copy = new_path;
	int j = 0;
	while(j < 50000){
		new_path.getNewSolution_variation();
		if(copy.getLength() < new_path.getLength()){
			copy = new_path;
		}
		j++;
	}
	for(int i = 0; i < 100; i++){
		int num = rand()%group_size;
		group[num] = copy;
	}
	 
}

// 读取txt文件，获取数据
void GA::init_dis() 
{
	int i, j;
	ifstream in("tspdata.txt");
	for (i = 0; i < nCities; i++)
	{
		in >> nodes[i].num >> nodes[i].x >> nodes[i].y;
	}

	for (i = 0; i < nCities; i++)
	{
		length_table[i][i] = (double)INT_MAX;
		for (j = i + 1; j < nCities; j++)
		{
			length_table[i][j] = length_table[j][i] = sqrt(
				(nodes[i].x - nodes[j].x) * (nodes[i].x - nodes[j].x) +
				(nodes[i].y - nodes[j].y) * (nodes[i].y - nodes[j].y));
		}
	}
}

// 选择优秀的种群
void GA::choose(vector<Path> & group){
	double sum_fitness = 0;
	double fitness[group_size];//适应性数组，用适应函数来计算
	double chance[group_size];//概率数组
	double pick;//用于轮盘赌的随机数
	vector<Path> next;

	for (int i = 0; i < group_size; i++) {
		fitness[i] = 1 / group[i].getLength();
		sum_fitness += fitness[i];
	}
	for (int i = 0; i < group_size; i++) {
		chance[i] = fitness[i] / sum_fitness;
	}
	//轮盘赌策略
	for (int i = 0; i < group_size; i++) {
		pick = ((double)rand()) / RAND_MAX;//0到1的随机数
		for (int j = 0; j < group_size; j++) {
			pick -= chance[j];
			// 不断往下选，当pick小于0就选该种群，chance越大越有机会
			if (pick <= 0) {
				next.push_back(group[j]);
				break;
			}
			//仍未选中，但是已经到最后一个了
			if (j == group_size - 1) {
				next.push_back(group[j]);
			}
		}
	}
	group = next;
}

// 交叉
void GA::cross(vector<Path> & group) {
	int point = 0;
	int choice1, choice2;
	while (point < group_size) {
		double pick = ((double)rand()) / RAND_MAX;//0到1的随机数
		if (pick > p_cross)
			continue;//判断是否交叉
		else {
			// 选择临近两个点来进行交叉
			// 可以改进为随机选择
			choice1 = point;
			choice2 = point + 1;
			group[choice1].getNewSolution_cross(group[choice2]);//交叉
		}
		point += 2;
	}
}

// 变异
void GA::variation(vector<Path> & group) {
	int point = 0;
	while (point < group_size) {
		double pick = ((double)rand()) / RAND_MAX;//0到1的随机数
		// 概率变异
		// 可以改进为随机选择
		if (pick < p_variation) {
			group[point].getNewSolution_variation();
		}
		point++;
	}
}

// 决定子代是否能取代亲本，获取的优秀种群
void GA::judge(vector<Path> & old_group, vector<Path> & group) {
	int point = 0;
	while (point < group_size) {
		if (old_group[point].getLength() < group[point].getLength())
			group[point] = old_group[point];
		point++;
	}
}

Path GA::getPath() {
	// 初始化随机数种子
	srand((unsigned)time(NULL));
	// 初始化种群
	init(); 

	ofstream outfile, outfile2;
	outfile2.open("result2.txt");
	outfile.open("result.txt");
	if (!outfile.is_open()) {
	  	printf("Open outfile failed!\n");
	}
	
	Path best;
	for (int i = 0; i < time_to_breed; i++) {
		vector<Path> old_group = group;
		// 选择
		choose(group);
		// 交叉
		cross(group);
		// 变异次数为5，引入新的个体
		for(int j = 0; j < 5; j++){
			variation(group);
			judge(old_group,group);
		}
		// 找出种群中的最优解
		for (int j = 0; j < group_size; j++) {
			group[j].Calculate_length();
			if (group[j].getLength() < best.getLength()){
				best = group[j];
			}
		}
		cout << "当前最优解为：" << best.getLength() << "  当前迭代次数为：" << i << endl;
		
		if(i % 100 == 0){	
			for (int i = 0; i < nCities; i++) {
				outfile << (i+1) << "\t" << best.getPath()[i] << endl; 
			}
		}
		outfile2 << best.getLength() << endl;
	}
	outfile2.close();
	outfile.close();
	return best;
}

int main() {
	time_t start, finish;
    start = clock(); // 开始计时
	GA process;
	process.init_dis();
	Path ans = process.getPath();
	bool count[nCities] = {false};
	int count2 = 0;
	for (int j = 0; j < nCities; ++j)
	{
		for (int i = 0; i < nCities; ++i)
		{
			if (ans.getPath()[i] == j+1)
			{
				count2++;
				break;
			}
		}
	}
	finish = clock(); //计时结束 
	if (count2 == nCities)
	{
		cout << endl << "Legal Solution!\n";
	}
	else{
		cout << endl << "Illegal Solution!\n";
		return 0;
	}
	double duration = ((double)(finish - start)) / CLOCKS_PER_SEC;
    cout << "Genetic Algorithm: " << endl;
    cout << "Best Path: " << endl;
  
	for (int i = 0; i < nCities-1; i++) {
		cout << ans.getPath()[i] << " -> ";
	}
	cout << ans.getPath()[nCities - 1];
	
	cout << "\nRelative Error: " << (ans.getLength() - BEST) / BEST << endl;

	cout << "\nBest Path Length: " << ans.getLength() << endl;

	cout << "Time for Algorithm: " << duration << "sec" << endl;
	cout << endl;
}



