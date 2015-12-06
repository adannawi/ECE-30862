#ifndef STATUSCONDITION_H_
#define STATUSCONDITION_H_

#include <map>
#include <string>
#include "Condition.h"
#include "rapidxml.hpp"

class StatusCondition : public Condition {
 private:
    void createCondition(xml_node<>*);
 public:
    StatusCondition(xml_node<>*);
    string status;
    bool evaluate(Zork&);
};


#endif
