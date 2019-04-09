package si.uni_lj.fri.pbd2019.runsup2;

final class MainHelper {

    private static  final float MpS_TO_MIpH = 2.23694f;
    private static  final float KM_TO_MI = 0.62137119223734f;
    private static  final float MIN_TO_MINpMI = 1.609344f;

    public static String formatDuration(long time) {
        long h = time / 3600;
        long min = 0;
        long sec = 0;
        if(h>0) {
            min=time/60-h*60;
            if(min>0) {
                sec=time-h*3600-min*60;
            }
            else {
                sec=time-h*3600;
            }
        }
        else {
            min= time*60;
            if(min>0) {
                sec=time-min*60;
            }
            else {
                sec=time;
            }
        }
        String ho = Long.toString(h);
        ho=ho+":";
        String mi = Long.toString(min);
        mi=mi+":";
        String se = Long.toString(sec);
        if(h<9) {

            ho = "0" + ho ;
        }
        else if(min<9) {

            mi="0" + mi ;
        }
        else if(sec<9) {
            se = "0" + se;
        }
        ho=ho.concat(mi);
        ho=ho.concat(se);
        return ho;
    }

    public static String formatDistance(double n) {
       double a = n/1000.0;
       double b = Math.round((a*100.0))/100.0;
       return Double.toString(b);
    }

    public static String formatPace(double n) {
        double a = Math.round((n*100.0)) / 100.0 ;
        return Double.toString(a);
    }

    public static String formatCalories(double n) {
        double a = Math.round(n);
        return Double.toString(a);
    }

    public static double kmTOMi(double n) {
        return n*KM_TO_MI;
    }

    public static double mpsTOMiph(double n) {
        return n*MpS_TO_MIpH;
    }

    public static double minpkmToMinpmi(double n) {
        return n*MIN_TO_MINpMI;
    }


}
