#ifndef ITEM_H_
#define ITEM_H_

#include "ZorkObject.h"
#include "rapidxml.hpp"
using namespace rapidxml;

class Item: public ZorkObject {
 private:
    void createItem(xml_node<>*);
    void createTurnOn(xml_node<>*);


 public:
    Item(xml_node<> *);
    Item();
    Item(const Item&);
    string writing;
    string turnOnMessage;
    string turnOnAction;
};



#endif
