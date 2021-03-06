import java.util.ArrayList;

public class Car {

    private Depot depot;
    private int maximumLoad, vehicleNumber, maximumDuration, x, y;
    public int currentLoad;
    public double currentDuration;

    public ArrayList<Customer> customerSequence;

    Car(int vehicleNumber, int maximumLoad, int maximumDuration, Depot depot) {
        this.vehicleNumber = vehicleNumber;
        this.maximumLoad = maximumLoad;
        this.depot = depot;
        this.currentDuration = 0.0;
        this.x = depot.getX();
        this.y = depot.getY();
        this.customerSequence = new ArrayList<>();

        if(maximumDuration == 0){
            this.maximumDuration = Integer.MAX_VALUE;
        }else{
            this.maximumDuration = maximumDuration;
        }
    }

    static Car[] createCopy(Car[] cars, Depot depot) {
        Car[] copy = new Car[cars.length];

        for (int i = 0; i < cars.length; i ++) {
            Car car = cars[i];
            copy[i] = new Car(car.vehicleNumber, car.maximumLoad, car.maximumDuration, depot);
        }

        return copy;
    }

    static Car[] createCopyWithCustomers(Car[] cars, Depot depot) {
        Car[] copy = new Car[cars.length];
        for (int i = 0; i < cars.length; i ++) {
            Car car = cars[i];
            copy[i] = new Car(car.vehicleNumber, car.maximumLoad, car.maximumDuration, depot);

            ArrayList<Customer> customerList = car.getCustomerSequence();

            for (Customer customer: customerList){
                copy[i].getCustomerSequence().add(customer);
            }
            copy[i].updateDistance();
            copy[i].updateLoad();
        }
        return copy;
    }

    static Car copyCar(Car car){
        Car copy = new Car(car.vehicleNumber, car.maximumLoad, car.maximumDuration, car.getDepot());
        copy.currentDuration = car.currentDuration;
        copy.currentLoad = car.currentLoad;

        ArrayList<Customer> customerList = car.getCustomerSequence();

        for (Customer customer: customerList){
            copy.getCustomerSequence().add(customer);
        }

        return copy;
    }

    void smartAddCustomerVisited(Customer customer, int index) {
        customerSequence.add(index, customer);
        currentDuration = checkDistance(customer, index);
        currentLoad += customer.getDemand();
    }

    void remove(Customer customer) {
        customerSequence.remove(customer);
        updateLoad();
        updateDistance();
    }


    //Check weather a car is valid
    Boolean isValid(){
        if(this.currentDuration <= this.maximumDuration && this.currentLoad <= this.maximumLoad){
            return true;
        }
        return false;
    }

    /**
     * Check if a customer can be added to a route and where it will be of lowest extra duration
     * @param customer
     * @return Index of where it is smartest to add the new customer
     */
    double[] smartCheckExtraDuration(Customer customer) {

        double minDuration = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < customerSequence.size() + 1; i ++) {
            final double duration = checkDistance(customer, i);
            if (duration < minDuration) {
                minDuration = duration;
                index = i;
            }
        }

        return new double[]{index, minDuration};
    }

    double checkDistance(Customer customer, int index) {
        double distance = 0;
        Customer previous = null;
        int count = 0;
        for (int i = 0; i < customerSequence.size() + 1; i ++) {
            Customer current;
            if (i == index) {
                current = customer;
            }
            else {
                current = customerSequence.get(count ++);
            }

            distance += current.getServiceDuration();

            if (previous == null) {
                distance += Population.euclideanDistance(depot.getX(), depot.getY(), current.getX(), current.getY());
            }
            else {
                distance += Population.euclideanDistance(previous.getX(), previous.getY(), current.getX(), current.getY());
            }

            previous = current;
        }

        distance += Population.euclideanDistance(previous.getX(), previous.getY(), depot.getX(), depot.getY());

        return distance;
    }

    void updateDistance() {
        if (customerSequence.size() == 0) {
            currentDuration = 0;
            return;
        }

        double distance = 0;
        Customer previous = null;
        int count = 0;
        for (int i = 0; i < customerSequence.size(); i ++) {
            Customer current = customerSequence.get(i);
            distance += current.getServiceDuration();
            if (previous == null) {
                distance += Population.euclideanDistance(depot.getX(), depot.getY(), current.getX(), current.getY());
            }
            else {
                distance += Population.euclideanDistance(previous.getX(), previous.getY(), current.getX(), current.getY());
            }

            previous = current;
        }

        distance += Population.euclideanDistance(previous.getX(), previous.getY(), depot.getX(), depot.getY());
        currentDuration = distance;
    }

    void updateLoad() {
        int load = 0;
        for (Customer customer : customerSequence) {
            load += customer.getDemand();
        }
        currentLoad = load;
    }

    /**
     * This method is used to determine if the car is eligible to add a customer to its route or not
     */
    double[] isEligible(Customer customer) {
        final double[] smartCheck = smartCheckExtraDuration(customer);
        final boolean durationCheck = smartCheck[1] <= maximumDuration;
        final boolean loadCheck = currentLoad + customer.getDemand() <= maximumLoad;
        if (!(durationCheck && loadCheck)) {
            smartCheck[0] = -1;
        }
        return smartCheck;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Customer> getCustomerSequence() {
        return customerSequence;
    }

//    public void setCustomerSequence(ArrayList<Customer> customerSequence) {
//        this.customerSequence = customerSequence;
//    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(double currentDuration) {
        this.currentDuration = currentDuration;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

//    public void setCurrentLoad(int currentLoad) {
//        this.currentLoad = currentLoad;
//    }

    public int getMaximumLoad() {
        return maximumLoad;
    }

    public void setMaximumLoad(int maximumLoad) {
        this.maximumLoad = maximumLoad;
    }

    public int getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(int maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }
}
