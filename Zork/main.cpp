#include "Zork.h"
#include "Item.h"

int main(int argc, char * argv[]){
    if (argc < 2) {
	cout << "Please input an XML file!" << endl;
	return 0;
    }

    string filename = string(argv[1]);
    Zork * game = new Zork(filename);
    delete game;

    return 0;
}
