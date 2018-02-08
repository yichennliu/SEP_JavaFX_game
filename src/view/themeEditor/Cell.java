package view.themeEditor;

import model.enums.Token;
import model.themeEditor.Theme;

public class Cell {

        private Token t;
        private Theme.FeldType f;

        public Cell(Token t, Theme.FeldType f){
            this.t = t;
            this.f = f;
        }
        public Cell(Token t){
            this.t =t;
        }
        public String toString(){
            if(hasFeldType()) return f.name();
            return t.name();
        }

        public Token getToken(){
            return this.t;
        }

        public Theme.FeldType getFeldType() {
            return this.f;
        }

        public boolean hasFeldType(){
            return (f!=null);
        }
}
