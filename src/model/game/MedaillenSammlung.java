package model.game;

import java.util.Map;

public class MedaillenSammlung {

    Map<String, Medaille>medaillenMap;

    public void put(Medaille medaille){
        String levelname = medaille.getLevelname();
        Medaille alteMedaille = medaillenMap.get(levelname);

        if(alteMedaille != null){
            int compare = alteMedaille.getColor().compareTo(medaille.getColor());
            if(compare > 0) return;

            if(compare == 0){
                if(medaille.getRatio()<alteMedaille.getRatio())
                    return;
            }
            medaillenMap.put(levelname, medaille);
        }

    }

}
