#ifndef ZORKOBJECT_H
#define ZORKOBJECT_H
#include <list>
#include <string>
#include "Trigger.h"
using namespace std;

class ZorkObject {

 public:
    string name;
    string status;
    string description;
    string type;
    list<Trigger *> triggers;
    ZorkObject();
    void configure(list<string> configMap);
    void setName(string s_name);
    string getName();
    void setStatus(string s_status);
    string getStatus();
    void setDescription(string s_description);
    string getDescription();
    string getType();
};

#endif
