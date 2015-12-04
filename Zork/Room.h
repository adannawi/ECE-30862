#ifndef ROOM_H_
#define ROOM_H_

#include "ZorkObject.h"
#include "rapidxml.hpp"
#include <string>
#include <map>

using namespace rapidxml;
class Room: public ZorkObject{
 private:
    void createRoom(xml_node<>*);
    void setBorder(xml_node<>*);
 public:
    Room(xml_node<>*);
    ~Room();
    map<string, string> borders;
    map<string, string> containers;
    map<string, string> items;
    map<string, string> creatures;
};

#endif
