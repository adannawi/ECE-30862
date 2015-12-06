#include "StatusCondition.h"
#include "Zork.h"

bool StatusCondition::evaluate(Zork& zork){
    ZorkObject * tempObj = zork.objects.find(object)->second;
    string tempStatus = tempObj->status;
    if ((tempObj != NULL) && (tempStatus == status)){
	return true;
    }
    return false;
}

StatusCondition::StatusCondition(xml_node<>* node){
    createCondition(node);
}

void StatusCondition::createCondition(xml_node<>* node){
    node = node->first_node();
    while (node != NULL){
	if (string(node->name()) == string("object")){
	    this->object = node->value();
	}
	if (string(node->name()) == string("status")){
	    this->status = node->value();
	}
	node = node -> next_sibling();
    }
}
