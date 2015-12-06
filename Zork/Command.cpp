#include "Command.h"
#include "Zork.h"

Command::Command(xml_node<> * node){
    command = node->value();
}
bool Command::evaluate(Zork& zork){
    if (command == zork.userIn){
	return true;
    }else{
	return false;
    }
}
