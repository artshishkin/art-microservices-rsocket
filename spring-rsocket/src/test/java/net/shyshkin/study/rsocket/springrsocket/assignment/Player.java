package net.shyshkin.study.rsocket.springrsocket.assignment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Player {

    private int lower = 0;
    private int upper = 100;
    private int mid = 0;
    private int attempts = 0;

    public void processResponse(GameResponse gameResponse) {

        switch (gameResponse) {
            case EQUAL:
                return;
            case LESS:
                lower = mid;
                break;
            case MORE:
                upper = mid;
                break;
        }

        attempts++;
        log.debug("{} : {} : {}", attempts, mid, gameResponse);
        emit();
    }

    private void emit() {
        mid = (lower + upper) / 2;
        //emit
    }

}
