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
#include "Creature.h"

using namespace rapidxml;
using namespace std;
class Zork {
 private:
    bool createGame(string filename);
    bool checkTriggers();
    void completeAction(string);
    void checkCommand(string);
 public: 
    bool completeFlag;
    Zork(string);
    Zork(Zork&);
    ~Zork();
    void begin();
    string userIn;
    string currRoom;
    map<string, string>inventory;
    map<string, Item*>items;
    map<string, Container*>containers;
    map<string, Room*>rooms;
    map<string, Creature*> creatures;
    map<string, ZorkObject*> objects;
    map<string, string> types;
};
#endif
