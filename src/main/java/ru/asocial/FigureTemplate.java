package ru.asocial;

class FigureTemplate {

    private int width;
    private int height;
    private byte[][] cells;
    private int alive;

    FigureTemplate(int width, int height, byte[][] cells, int alive){
        this.width = width;
        this.height = height;
        this.cells = cells;
        this.alive = alive;
    }

    int render(byte[][] array, int x, int y){
        return render(array, x, y, false, false);
    }

    int render(byte[][] array, int x, int y, boolean flipX, boolean flipY){
        for (int i = 0; i< width; i++) {
            for (int j = 0; j < height; j++) {
                array[x + i][y + j] = cells[flipX ? width - 1 - i : i][flipY ? height - 1 - j : j];
            }
        }
        return alive;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

