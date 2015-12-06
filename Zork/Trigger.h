#ifndef TRIGGER_H_
#define TRIGGER_H_

#include <string>
#include <list>

#include "Condition.h"
#include "StatusCondition.h"
#include "HasCondition.h"
#include "Command.h"
#include "rapidxml.hpp"
using namespace std;
using namespace rapidxml;

class Trigger{
 private:
    void createTrigger(xml_node<> *);
    void addCondition(xml_node<> *);
 public:
    Trigger(xml_node<>*);
    ~Trigger();
    list<Condition *> conditions;
    string type;
    bool hasCommand;
    string print;
    list<string> action;
    bool evaluate(Zork&);
};

#endif
