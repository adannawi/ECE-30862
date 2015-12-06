#include "HasCondition.h"
#include "Zork.h"

HasCondition::HasCondition(xml_node<>* node){
    createCondition(node);
}

void HasCondition::createCondition(xml_node<>* node){
    node = node -> first_node();
    while (node != NULL){
	if (string(node->name()) == string("has")){
		this->has = node->value();
	}
	if (string(node->name()) == string("object")){
	    this->object = node->value();
	}
	if (string(node->name()) == string("owner")){
	    this->owner = node->value();
	}
	node = node -> next_sibling();
    }
}

bool HasCondition::evaluate(Zork& zork){
    if (owner == "inventory"){
	if (zork.inventory.find(object) != zork.inventory.end()){
	    if(has == "yes" || has == "no"){
		return true;
	    }
	}
	return false;
    }

    if (zork.rooms.find(owner) != zork.rooms.end()){
	Room * roomPtr = zork.rooms.find(owner)->second;
        bool isItemThere = (roomPtr->items.find(object) != roomPtr->items.end());
	bool isItemOkay = (has == "yes");
	if (isItemThere && isItemOkay || !isItemThere && !isItemOkay){
	    return true;
	}else{
	    return false;
	}
    }

    if (zork.containers.find(owner) != zork.containers.end()){
	Container * contPtr = zork.containers.find(owner)->second;
	bool isItemThere = (contPtr->items.find(object) != contPtr->items.end());
	bool isItemOkay = (has == "yes");
	if (isItemThere && isItemOkay || !isItemThere && !isItemOkay){
	    return true;
	}else{
	    return false;
	}
    }
}
	    
