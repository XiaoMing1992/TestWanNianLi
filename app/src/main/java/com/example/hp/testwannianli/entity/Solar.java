package com.example.hp.testwannianli.entity;


/**
 *
 *
 * Created by claptrap on 2016-9-6.
 */
public class Solar {
    private final int[] strings = new int[]{
            0x04AE53,0x0A5748,0x5526BD,0x0D2650,0x0D9544,0x46AAB9,0x056A4D,0x09AD42,0x24AEB6,0x04AE4A,
            0x6A4DBE,0x0A4D52,0x0D2546,0x5D52BA,0x0B544E,0x0D6A43,0x296D37,0x095B4B,0x749BC1,0x049754,
            0x0A4B48,0x5B25BC,0x06A550,0x06D445,0x4ADAB8,0x02B64D,0x095742,0x2497B7,0x04974A,0x664B3E,
            0x0D4A51,0x0EA546,0x56D4BA,0x05AD4E,0x02B644,0x393738,0x092E4B,0x7C96BF,0x0C9553,0x0D4A48,
            0x6DA53B,0x0B554F,0x056A45,0x4AADB9,0x025D4D,0x092D42,0x2C95B6,0x0A954A,0x7B4ABD,0x06CA51,
            0x0B5546,0x555ABB,0x04DA4E,0x0A5B43,0x352BB8,0x052B4C,0x8A953F,0x0E9552,0x06AA48,0x6AD53C,
            0x0AB54F,0x04B645,0x4A5739,0x0A574D,0x052642,0x3E9335,0x0D9549,0x75AABE,0x056A51,0x096D46,
            0x54AEBB,0x04AD4F,0x0A4D43,0x4D26B7,0x0D254B,0x8D52BF,0x0B5452,0x0B6A47,0x696D3C,0x095B50,
            0x049B45,0x4A4BB9,0x0A4B4D,0xAB25C2,0x06A554,0x06D449,0x6ADA3D,0x0AB651,0x093746,0x5497BB,
            0x04974F,0x064B44,0x36A537,0x0EA54A,0x86B2BF,0x05AC53,0x0AB647,0x5936BC,0x092E50,0x0C9645,
            0x4D4AB8,0x0D4A4C,0x0DA541,0x25AAB6,0x056A49,0x7AADBD,0x025D52,0x092D47,0x5C95BA,0x0A954E,
            0x0B4A43,0x4B5537,0x0AD54A,0x955ABF,0x04BA53,0x0A5B48,0x652BBC,0x052B50,0x0A9345,0x474AB9,
            0x06AA4C,0x0AD541,0x24DAB6,0x04B64A,0x69573D,0x0A4E51,0x0D2646,0x5E933A,0x0D534D,0x05AA43,
            0x36B537,0x096D4B,0xB4AEBF,0x04AD53,0x0A4D48,0x6D25BC,0x0D254F,0x0D5244,0x5DAA38,0x0B5A4C,
            0x056D41,0x24ADB6,0x049B4A,0x7A4BBE,0x0A4B51,0x0AA546,0x5B52BA,0x06D24E,0x0ADA42,0x355B37,
            0x09374B,0x8497C1,0x049753,0x064B48,0x66A53C,0x0EA54F,0x06B244,0x4AB638,0x0AAE4C,0x092E42,/*2051-2060*/
            0x3C9735,0x0C9649,0x7D4ABD,0x0D4A51,0x0DA545,0x55AABA,0x056A4E,0x0A6D43,0x452EB7,0x052D4B,/*2061-2070*/
            0x8A95BF,0x0A9553,0x0B4A47,0x6B553B,0x0AD54F,0x055A45,0x4A5D38,0x0A5B4C,0x052B42,0x3A93B6,/*2071-2080*/
            0x069349,0x7729BD,0x06AA51,0x0AD546,0x54DABA,0x04B64E,0x0A5743,0x452738,0x0D264A,0x8E933E,/*2081-2090*/
            0x0D5252,0x0DAA47,0x66B53B,0x056D4F,0x04AE45,0x4A4EB9,0x0A4D4C,0x0D1541,0x2D92B5
    };
    // 十六进制转二进制
    public String toB(int number){
        StringBuffer exstring = new StringBuffer();
        final int length = 6;
        StringBuffer binary;
        binary = new StringBuffer(Integer.toBinaryString(number));
        if (binary.length() < length * 4){
            int a = length * 4 - binary.length();
            char[] c = new char[a];
            for (int i = 0; i < a; i++) {
                c[i] = '0';
            }
            exstring = new StringBuffer(String.valueOf(c));
            exstring.append(binary);
        }
        return exstring.toString();
    }

    /**
     * 计算某年某月有多少天
     * @param year
     * 年份
     * @param month
     * 月份
     * @return
     * 返回天数
     */
    public static int daysOfMonth(int year, int month) {
        switch (month){
            case 1:
                return 31;
            case 2:
                if (isLeapYear(year))
                    return 29;
                return 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                return 30;
        }
    }
    /**
     * 这个月对应的农历日期
     * @param year
     * 年份
     * @param month
     * 月份
     * @return
     * 返回本月农历日期的数组
     */
    public int[] days(int year, int month){
        int number = daysOfMonth(year,month);
        int[] solar_days = new int[number];
        int month_quantity ;
        String spring = getSpringFestival(year);
        String yearB = toB(strings[year-1901]);
//        春节距离这个月有多少天
        int spring_to_this_month;
        int day_s = Integer.valueOf(spring.substring(2,4));
        int month_s = Integer.valueOf(spring.substring(0,2));
//        lit_month表示闰月月份
        int lit_month = BtoD(yearB.substring(0,4));
        if (lit_month == 0){
//            12个月
            month_quantity = 12;
        }
        else{
//            13个月
            month_quantity = 13;
        }
        if (month > month_s){
            spring_to_this_month = dayCount(year,month,1)-dayCount(year,month_s,day_s);
            for (int i = 0; i < month_quantity; i++) {
                if ((spring_to_this_month - howManyDays(i+1,yearB) >= 0)){
                    spring_to_this_month -= howManyDays(i+1,yearB);
                }
                else{
                    int y = 1;
                    for (int j = 0; j < solar_days.length; j++) {
                        if (spring_to_this_month < howManyDays(i+1,yearB)){
                            solar_days[j] = ++spring_to_this_month ;
                        }
                        else if (spring_to_this_month >= howManyDays(i+1,yearB)){
                            if (y <= howManyDays(i+1,yearB)){
                                solar_days[j] = y++;
                            }
                            else {
                                y = 1;
                            }
                        }
                    }
                    break;
                }
            }
        }
        else if(month == month_s){
            spring_to_this_month = dayCount(year,month_s,day_s) - dayCount(year,month,1);
            int y = 1;
            for (int i = day_s-1; i < solar_days.length; i++) {
                solar_days[i] = y++;
            }
            y = howManyDays(month_quantity,toB(strings[year-1901-1]));
            for (int i = day_s-2; i >= 0 ; i--) {
                if (y < spring_to_this_month){
                    solar_days[i] = y--;
                    if (y == 0){
                        y = howManyDays(month_quantity-1,toB(strings[year-1901-1]));
                    }
                }
                else {
                    solar_days[i] = y--;
                }
            }
        }
        else if(month < month_s){
            lit_month = BtoD(toB(strings[year-1901-1]).substring(0,4));
            if (lit_month == 0){
//            12个月
                month_quantity = 12;
            }
            else{
//            13个月
                month_quantity = 13;
            }
            spring_to_this_month = dayCount(year,month_s,day_s) - dayCount(year,month,1);
            int y;
            for (int i = month_quantity; i > 0; i--) {
                if (howManyDays(month_quantity,toB(strings[year-1901-1])) < spring_to_this_month){
                    spring_to_this_month -= howManyDays(month_quantity,toB(strings[year-1901-1]));
                }
                else{
                    y = howManyDays(i,toB(strings[year-1901-1])) - spring_to_this_month + 1;
                    for (int j = solar_days.length-1; j >= 0; j--) {
                        solar_days[j] = y--;
                        if (y == 0){
                            y = howManyDays(i--,toB(strings[year-1901-1]));
                        }
                    }
                    break;
                }
            }
        }
        return solar_days;
    }
    private int dayCount(int year, int month,int day){
        int days = 0;
        switch (month){
            case 12:
                days += 30;
                month--;
            case 11:
                days += 31;
                month--;
            case 10:
                days += 30;
                month--;
            case 9:
                days += 31;
                month--;
            case 8:
                days += 31;
                month--;
            case 7:
                days += 30;
                month--;
            case 6:
                days += 31;
                month--;
            case 5:
                days += 30;
                month--;
            case 4:
                days += 31;
                month--;
            case 3:
                if(isLeapYear(year))
                    days += 29;
                else
                    days += 28;
                --month;
            case 2:
                days += 31;
                --month;
            case 1:
                days += day;
                break;
        }
        return days;
    }
    public int howManyDays(int i,String yearB){
        if(Integer.valueOf(yearB.substring(i+3, i+4)) == 0)
            return 29;
        else
            return 30;
    }
    public static boolean isLeapYear(int year){
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }
    public static void main(String[] args){
        Solar solar = new Solar();
        System.out.println("2016"+solar.toB(solar.strings[2016-1901]));
        System.out.println("Spring Festival is "+solar.getSpringFestival(2016));
        int[] days = solar.days(2016,12);
        for (int i = 0; i < days.length; i++) {
            System.out.println((i+1)+"--------"+days[i]);
        }
    }
    public String getSpringFestival(int year){
        String yearB = toB(strings[year-1901]);
        String day = yearB.substring(19,24);
        String month = yearB.substring(17,19);
        int int_day = BtoD(day);
        int int_month = BtoD(month);
        String flag = String.valueOf(int_month);
        if (flag.length() == 1)
            flag = "0"+flag;
        String flag_day = String.valueOf(int_day);
        if (flag_day.length() == 1)
            flag_day = "0"+flag_day;
        flag = flag + flag_day;
        return flag;
    }
    // 二进制字符串转十进制数
    public int BtoD(String day){
        int int_day = 0;
        for (int i = 0; i < day.length()-1; i++) {
            int_day = (int_day+Integer.valueOf(day.substring(i,i+1)))*2;
        }
        if (Integer.valueOf(day.substring(day.length()-1,day.length())) == 1)
            int_day++;
        return int_day;
    }
// 任意进制数转为十进制数
//    public String toD(int number){
//        return String.valueOf(number);
//    }
}
