package ru.epatko.statistics.model;

import java.time.LocalDate;

public class Quotation implements Comparable<Quotation> {

    private final String instrument;
    private final LocalDate date;
    private final double value;

    public Quotation(String instrument, LocalDate date, double value) {
        this.instrument = instrument;
        this.date = date;
        this.value = value;
    }

    public String getInstrument() {
        return instrument;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quotation)) return false;

        Quotation quotation = (Quotation) o;

        if (Double.compare(quotation.value, value) != 0) return false;
        if (instrument != null ? !instrument.equals(quotation.instrument) : quotation.instrument != null) return false;
        return date != null ? date.equals(quotation.date) : quotation.date == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = instrument != null ? instrument.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Quotation{" +
                "instrument='" + instrument + '\'' +
                ", date=" + date +
                ", value=" + value +
                '}';
    }

    @Override
    public int compareTo(Quotation o) {
        if (this.instrument == null) return 1;
        if (o.instrument == null) return -1;
        return o.getDate().compareTo(this.date);
    }
}
