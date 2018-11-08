package net.betainteractive.io.readers

/**
 * Created by pfilimone on 08-07-2014.
 */
class CSVReport<T> {
    boolean hasErrors
    String errorDescription
    int errorNumber
    def List<T> values = []
}
