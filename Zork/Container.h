#ifndef CONTAINER_H_
#define CONTAINER_H_

#include "rapidxml.hpp"
#include <string>
#include <list>
#include <map>
#include "ZorkObject.h"
using namespace rapidxml;

class Container: public ZorkObject{
 private:
    void createContainer(xml_node<>*);
 public:
    Container(xml_node<>*);
    ~Container();
    list<string> accept;
    map<string, string> items;
    bool openState;
};

#endif
