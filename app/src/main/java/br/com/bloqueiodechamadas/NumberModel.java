package br.com.bloqueiodechamadas;

class NumberModel {
    private boolean isSelected;
    private String number;

    boolean getSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        isSelected = selected;
    }

    String getNumber() {
        return number;
    }

    void setNumber(String number) {
        this.number = number;
    }
}
