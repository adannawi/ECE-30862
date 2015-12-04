#ifndef ZORK_H_
#define ZORK_H_
#include <map>
#include <string>
#include <queue>
#include <iostream>
#include <stdio.h>
#include <fstream>
#include <vector>
#include <sstream>
#include <iterator>
#include <list>
#include "rapidxml.hpp"
#include "rapidxml_print.hpp"
#include "rapidxml_utils.hpp"
#include "Item.h"
#include "Container.h"
#include "Room.h"

using namespace rapidxml;
using namespace std;
class Zork {
 private:
    bool createGame(string filename);
 public: 
    bool completeFlag;
    Zork(string);
    ~Zork();
    void Go();
    string UserIn;
    string currRoom;
    map<string, string>inventory;
    map<string, Item*>items;
    map<string, Container*>containers;
    map<string, Room*>rooms;
};
#endif
