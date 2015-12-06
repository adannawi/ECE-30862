#ifndef HASCONDITION_H_
#define HASCONDITION_H_

#include <map>
#include <list>
#include "Condition.h"
#include "rapidxml.hpp"

class HasCondition: public Condition {
 private:
    bool checkInv(Zork&);
    bool checkCont(Zork&);
    bool checkRoom(Zork&);
    void createCondition(xml_node<>*);

 public:
    HasCondition(xml_node<>*);
    //   ~HasCondition();

    string has;
    string owner;
    bool evaluate(Zork&);
};










#endif
