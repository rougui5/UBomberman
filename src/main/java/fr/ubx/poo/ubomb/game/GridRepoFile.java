package fr.ubx.poo.ubomb.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GridRepoFile extends GridRepo{
    public GridRepoFile(Game game){
        super(game);
    }

    @Override
    public Grid load(int level, String name) {
        int width = 0; int height = 0;

        try {
            //read through the file which corresponds to the level
            BufferedReader br = new BufferedReader(new FileReader(this.game.samplePath + "/" + name));
            StringBuilder builder = new StringBuilder();
            String line = br.readLine();
            //determines the width and the height of the grid
            width = line.length();
            while(line != null){
                height++;
                builder.append(line);
                builder.append('x');
                line = br.readLine();
            }
            //get the string which contains the entire level
            String str = builder.toString();
            Grid grid = new Grid(width, height);

            //put the elements corresponding to the code (in the string) in the grid
            int count = 0;
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    Position pos = new Position(j, i);
                    //specifies the levels start position
                    if(str.charAt(count) == 'V') game.playerInitialPositions.put(level, pos);
                    else if(str.charAt(count) == 'n') game.playerNextPositions.put(level, pos);
                    EntityCode entity = EntityCode.fromCode(str.charAt(count));
                    count++;
                    grid.set(pos, processEntityCode(entity, pos));
                }
                count++;
            }
            return grid;
        } catch (FileNotFoundException e){
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
