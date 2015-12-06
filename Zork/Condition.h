#ifndef CONDITION_H_
#define CONDITION_H_

#include <map>
#include <list>
#include <string>
#include "rapidxml.hpp"

class Zork;

using namespace std;
using namespace rapidxml;

class Condition{
 public:
    string object;
    virtual bool evaluate(Zork&) = 0;
    virtual ~Condition(){};
};

#endif
