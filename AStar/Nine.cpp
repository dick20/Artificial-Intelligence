#include <iostream>
#include <vector>
#include <time.h>
#include <algorithm>
#include <cmath>
using namespace std;

const int GRID = 3; // 3*3 grid
//int rightPos[9] = {4, 0, 1, 2, 5, 8, 7, 6, 3};
int rightPos[9] = {4, 0, 1, 2, 5, 8, 7, 6, 3};
// 目标状态时，若panel[i][j]=OMG，那么3*i+j = rightPos[OMG]


struct state {
  int panel[GRID][GRID];
  int level; // depth
  int h;
  state *parent;

  state(int level) : level(level) {}

  bool operator==(state &q) {
    // Whehther two state are equivalent
    for (int i = 0; i < GRID; i++) {
      for (int j = 0; j < GRID; j++) {
        if (panel[i][j] != q.panel[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

  state& operator=(state &p) {
    for (int i = 0; i < GRID; i++) {
      for (int j = 0; j < GRID; j++) {
        panel[i][j] = p.panel[i][j];
      }
    }
    return *this;
  }
};

vector<state*> openTable; // open table
vector<state*> closeTable; // close table

void printPanel(state* p) {
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
      cout << p->panel[i][j] << " ";
    }
    cout << endl;
  }
}

// H: Manhattan Distance
int computeH(state &p) {
  int h = 0;
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
        h += abs(rightPos[p.panel[i][j]] / GRID - i);
        h += abs(rightPos[p.panel[i][j]] % GRID - j);
    }
  }
  return h;
}

// H2: Euclidean Distance
int computeH1(state &p) {
  int h = 0;
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
        int distance = (rightPos[p.panel[i][j]] / GRID - i) * (rightPos[p.panel[i][j]] / GRID - i)
                        + (rightPos[p.panel[i][j]] % GRID - j) * (rightPos[p.panel[i][j]] % GRID - j);
        distance = pow(distance, 0.5);
        h += distance;
    }
  }
  return h;
}

// H2: number of wrong position
int computeH2(state &p) {
  int h = 0;
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
        if ((3 * i + j) != rightPos[p.panel[i][j]]) {
          h++;
        }
    }
  }
  return h;
}

// Compute the f value of state p
int computeF(state *p) {
  // f = g + h
  return computeH2(*p) + p->level;
}

// return the zero position's index in list
int findZero(state &p) {
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
      if (p.panel[i][j] == 0) {
        return i * 3 + j;
      }
    }
  }
}

// Compare p value of two state
bool compareState(state *p, state *q) {
  return computeF(p) > computeF(q);
}

vector<state*>::iterator findDuplicate(vector<state*> &vec, state *p) {
  vector<state*>::iterator iter;
  for (iter = vec.begin(); iter != vec.end(); iter++) {
    // find duplicate
    if ((*(*iter)) == *p) {
      break;
    }
  }
  return iter;
}

bool isCanSolve(state &start) {
  int temp[9]= {0};
  int k = 0;
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
      temp[k++] = start.panel[i][j];
    }
  }
  int inverseNum = 0;
  for (int i = 0; i < 9; i++) {
    for (int j = i + 1; j < 9; j++) {
      if (temp[i] != 0 && temp[j] != 0 && temp[i] > temp[j]) {
        inverseNum++;
      }
    }
  }
  return (inverseNum % 2 != 0);
}

// apply AStar Algorithm
state* AStar(state &start) {
  int level = 0;
  openTable.push_back(&start);
  int count = 0;

  while (!openTable.empty()) {
    cout << "OpenTable Size: " << openTable.size() << endl;

    // Find the highest f value
    sort(openTable.begin(), openTable.end(), compareState);

    state *p = openTable.back();
    
    openTable.pop_back();

    // Reach target state
    if (computeH2(*p) == 0) {
      return p;
    }

    level = p->level + 1;

    int zeroPos = findZero(*p);
    int zeroX = zeroPos / 3;
    int zeroY = zeroPos % 3;
    for (int i = 0; i < 4; i++) {
      int x_offset = 0, y_offset = 0;
      switch (i) {
        case 0:
          // right
          x_offset = 0;
          y_offset = 1;
          break;
        case 1:
          // left
          x_offset = 0;
          y_offset = -1;
          break;
        case 2:
          // down
          x_offset = 1;
          y_offset = 0;
          break;
        case 3:
          // up
          x_offset = -1;
          y_offset = 0;
          break;
        default:
          break;
      }

      // out of bound
      if (zeroX + x_offset < 0 || zeroX + x_offset >= GRID || zeroY + y_offset < 0 || zeroY + y_offset >= GRID) {
        continue;
      }
      state *q = new state(level); // Initial a new state
      q->parent = p;
      *q = *p;
      // move zero to the new position
      q->panel[zeroX][zeroY] = q->panel[zeroX + x_offset][zeroY + y_offset];
      q->panel[zeroX + x_offset][zeroY + y_offset] = 0;
      if (!isCanSolve(*q)) {
        continue;
      }
      bool isSkip = false;
      vector<state*>::iterator duplicate = findDuplicate(openTable, q);
      // If q is in OpenTable, update it
      if (duplicate != openTable.end()) {
        if (computeF(q) < computeF(*duplicate)) {
          (*duplicate)->level = q->level;
          (*duplicate)->parent = q->parent;
        }
        isSkip = true;
      }
      // If q is in CloseTable, update it
      duplicate = findDuplicate(closeTable, q);
      if (duplicate != closeTable.end()) {
        if (computeF(q) < computeF(*duplicate)) {
          delete *duplicate;
          closeTable.erase(duplicate);
          //cout << "Test: " << q->panel[1][1] << endl;
          openTable.push_back(q);
          isSkip = true;
        }
      }

      if (!isSkip) {
        openTable.push_back(q);
      }
    }

    closeTable.push_back(p);
  }
}

void printSolution(state *q) {
  vector<state*> path;
  while (q) {
    path.push_back(q);
    q = q->parent;
  }

  int steps = 0;
  while (!path.empty()) {
    cout << "Step: " << steps << endl;
    // Print panel
    printPanel(path.back());
    cout << "h: " << computeH2(*path.back()) << "\tg: " << steps << "\tf: " << computeF(path.back()) << endl << endl;
    path.pop_back();
    steps++;
  }
}


int main() {
  cout << "Nine Puzzle!" << endl << endl;
  state start(0);
  state *target;

  // Initialize start state
  for (int i = 0; i < GRID; i++) {
    for (int j = 0; j < GRID; j++) {
      cin >> start.panel[i][j];
    }
  }
  /*
  start.panel[0][0] = 2;
  start.panel[0][1] = 1;
  start.panel[0][2] = 6;
  start.panel[1][0] = 4;
  start.panel[1][1] = 0;
  start.panel[1][2] = 8;
  start.panel[2][0] = 7;
  start.panel[2][1] = 5;
  start.panel[2][2] = 3;
  */
  start.parent = NULL;
  /*
  if (!isCanSolve(start)) {
    cout << "Can't Solve!" << endl;
  }
  */
  //else {
    float runTime = 0;
    time_t startTime = clock();

    target = AStar(start);
    
    time_t endTime = clock();
    runTime = endTime - startTime;
    cout << endl << "Total Run Time: " <<  runTime << " ms." << endl;
    printSolution(target);
  //}
  return 0;
}