#ifndef COMMAND_H_
#define COMMAND_H_
#include "rapidxml.hpp"
#include <string>
#include "Condition.h"

class Command : public Condition{
 public:
    bool evaluate(Zork&);
    Command(xml_node<>*);
    string command;
};

#endif
