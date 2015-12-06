#ifndef CREATURE_H_
#define CREATURE_H_

#include <iostream>
#include <map>
#include <list>
#include "rapidxml.hpp"
#include "ZorkObject.h"
#include "Condition.h"
using namespace rapidxml;
using namespace std;

class Creature: public ZorkObject{
 private:
    void createCreature(xml_node<>*);
    void addCondition(xml_node<>*);
    void getAttacked(xml_node<>*);
 public:
    Creature(xml_node<>*);
    ~Creature();
    map<string, string>vulnerabilities;
    list<Condition *>conditions;
    string print;
    list<string> action;
    bool attack(Zork&, string);
};


#endif
