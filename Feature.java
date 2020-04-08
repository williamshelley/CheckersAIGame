
interface Feature {
    double calculate(Board b);
    default double defaultCalculate(){
        return 0.0;
    }
}