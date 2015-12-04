#include <iostream>

#include "Zork.h"
#include "Item.h"
#include "Container.h"
#include "Room.h"
#include "Creature.h"

Zork::Zork(string filename){
    try{
	createGame(filename);
    }catch(int error){
	std::cout << "Something went wrong while loading the XML file." << std::endl;
    }
}

Zork::~Zork(){
}

bool Zork::createGame(string filename){
    xml_document<> game;
    xml_node<> * root_node;

    //Load XML file
    ifstream zorkFile(filename.c_str());
    if (!zorkFile.is_open()){
	cout << "Error opening file" << endl;
	return false;
    }
    //Do some magic
    vector<char> buffer((istreambuf_iterator<char>(zorkFile)), istreambuf_iterator<char>());
    buffer.push_back('\0');
    game.parse<0>(&buffer[0]);
    
    //Set root node
    root_node = game.first_node();
    
    //Initiate first node 
    xml_node<> * node = root_node -> first_node();

    list<xml_node<>*> rooms_list;
    list<xml_node<>*> items_list;
    list<xml_node<>*> containers_list;
    list<xml_node<>*> creatures_list;

    //Push rooms, items, containers, and creatures into respective lists
    while (node != NULL){
	if (string((node->name())) == string("room")){
	    rooms_list.push_front(node);
	    //  cout << "Room: " << node->first_node("name")->value() << endl; //Print out everything in rooms list
	}

	if (string((node->name())) == string("item")){
	    items_list.push_front(node);
	    // cout << "Item: " << node->first_node("name")->value() << endl; //Print items
	}

	if (string((node->name())) == string("container")){
	    containers_list.push_front(node);
	    // cout << "Container: " << node->first_node("name")->value() << endl; //Print containers
	}

	if (string((node->name())) == string("creatures")){
	    creatures_list.push_front(node);
	    // cout << "Creature: " << node->first_node("name")->value() << endl; //Print creatures
	}
	node = node -> next_sibling();
    }


    //Create Item Objects
    Item * newItem;
    for (list<xml_node<>*>::iterator it = items_list.begin(); it != items_list.end(); it++){
	newItem = new Item(*it);
	newItem->type = "item";
	items[newItem->name] = newItem;
    }

    //Create Container Objects
    Container * newContainer;
    for (list<xml_node<>*>::iterator it = containers_list.begin(); it != containers_list.end(); it++){
	newContainer = new Container(*it);
	newContainer->type = "container";
	containers[newContainer->name] = newContainer;
    }

    //Create Room Objects
    Room * newRoom;
    for (list<xml_node<>*>::iterator it = rooms_list.begin(); it != rooms_list.end(); it++){
	newRoom = new Room(*it);
	newRoom -> type = "room";
	rooms[newRoom->name] = newRoom;
    }

    //Create creature objects
    Creature * newCreat;
    for (list<xml_node<>*>::iterator it = creatures_list.begin(); it != creatures_list.end(); it++){
	newCreat = new Creature(*it);
	newCreat -> type = "creature";
	creatures[newCreat->name] = newCreat;
    }

    return true;

}
