package com.carry.customerflow.controller;

import com.carry.customerflow.bean.*;
import com.carry.customerflow.mapper.Shop_dataMapper;
import com.carry.customerflow.utils.DataUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class Shop_DataController {

    @Autowired
    private Shop_dataMapper shop_dataMapper;

    @Autowired
    private DataUtil dataUtil;

    @GetMapping("/getMainData")
    public Msg getMainData(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try{
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

//            System.out.println(dataUtil.getDayDiffer(Date.valueOf(dateTime),new Date(System.currentTimeMillis())));

            Integer walker_number = 0;
            Integer customer_number = 0;
            Integer new_customer = 0;
            Integer jumpout = 0;
            List<Shop_data> shop_dataList = shop_dataMapper.getMainData(address,dateTime);
            if (shop_dataList.size()==0)
                return Msg.failure("门店未添加或已经被删除").setCode(402);
            for (Shop_data shop_data:shop_dataList) {
                walker_number+=shop_data.getWalker_number();
                customer_number+=shop_data.getCustomer_number();
                new_customer+=shop_data.getNew_customer();
                jumpout+=shop_data.getJump_out();
            }
            Integer dynamicconsumer = shop_dataList.get(0).getDynamic_customer();
            Map<String,Integer> map=new HashMap<>();
            map.put("walkerNumber",walker_number);
            map.put("consumerNumber",customer_number);
            map.put("newConsumer",new_customer);
            map.put("jmpOut",jumpout);
            map.put("dynamicConsumer",dynamicconsumer);


            return Msg.success().setData(map);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    @GetMapping("/getCustomerPerHour")
    public Msg getCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<CustomerPerHour> customerPerHourList = shop_dataMapper.getCustomerPerHour(address, dateTime);
            Collections.reverse(customerPerHourList);
            return Msg.success().setData(customerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    @GetMapping("/getInCustomerPerHour")
    public Msg getInCustomerPerHour(@RequestParam("address")String address, @Param("dateTime")String dateTime){
        try {
            List<InCustomerPerHour> inCustomerPerHourList = shop_dataMapper.getInCustomerPerHour(address, dateTime);
            Collections.reverse(inCustomerPerHourList);
            return Msg.success().setData(inCustomerPerHourList);
        }catch (Exception e)
        {
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

//    ------------------------------日表

    /**
     * 获取全部的日表
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getAllDayComparison")
    public Msg getAllDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
        Integer walker_number = 0;
        Integer customer_number = 0;
        Integer jump_out = 0;
        Integer new_customer = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Comparison> walker_numberComparisonList = new ArrayList<>();
        List<Comparison> customer_numberComparisonList = new ArrayList<>();
        List<Comparison> jump_outComparisonList = new ArrayList<>();
        List<Comparison> new_customerComparisonList = new ArrayList<>();

        for (int i = 0;i<10;i++){
             walker_number = 0;
             customer_number = 0;
             jump_out = 0;
             new_customer = 0;
            List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
            if (shop_dataList.size()==0)
                break;
            for (Shop_data shopData:shop_dataList) {
                walker_number += shopData.getWalker_number();
                customer_number += shopData.getCustomer_number();
                jump_out += shopData.getJump_out();
                new_customer += shopData.getNew_customer();
            }
            String date = format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()));
            Comparison walker_numberComparison = Comparison.builder().date(date).number(walker_number).build();
            Comparison customer_numberComparison = Comparison.builder().date(date).number(customer_number).build();
            Comparison jump_outComparison = Comparison.builder().date(date).number(jump_out).build();
            Comparison new_customerComparison = Comparison.builder().date(date).number(new_customer).build();
//            加入数组
            walker_numberComparisonList.add(walker_numberComparison);
            customer_numberComparisonList.add(customer_numberComparison);
            jump_outComparisonList.add(jump_outComparison);
            new_customerComparisonList.add(new_customerComparison);
        }

//            倒顺序
            Collections.reverse(walker_numberComparisonList);
            Collections.reverse(customer_numberComparisonList);
            Collections.reverse(jump_outComparisonList);
            Collections.reverse(new_customerComparisonList);
//            加入map
        Map<String,List<Comparison> > comparisonMap = new HashMap<>();
            comparisonMap.put("walker_number",walker_numberComparisonList);
            comparisonMap.put("customer_number",customer_numberComparisonList);
            comparisonMap.put("jump_out",jump_outComparisonList);
            comparisonMap.put("new_number",new_customerComparisonList);

            return Msg.success(comparisonMap);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }



    /**
     * 返回人流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getWalker_numberDayComparison")
    public Msg getWalker_numberDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Integer j = 0;
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer walker_number = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0;i<10;i++){
                walker_number = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                if (shop_dataList.size()==0)
                    break;
                for (Shop_data shopData:shop_dataList)
                    walker_number += shopData.getWalker_number();

                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(walker_number).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回客流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getCustomer_numberDayComparison")
    public Msg getCustomer_numberDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer customer_number = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0;i<10;i++){
                customer_number = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                if (shop_dataList.size()==0)
                    break;
                for (Shop_data shopData:shop_dataList)
                    customer_number += shopData.getCustomer_number();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(customer_number).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getJump_outDayComparison")
    public Msg getJump_outDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer jump_out = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0;i<10;i++){
                jump_out = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                if (shop_dataList.size()==0)
                    break;
                for (Shop_data shopData:shop_dataList)
                    jump_out += shopData.getJump_out();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(jump_out).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 返回跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getNew_customerDayComparison")
    public Msg getnew_customerDayComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            Date date = null;
            List<Comparison> comparisonList = new ArrayList<>();
            Integer new_customer = 0;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0;i<10;i++){
                new_customer = 0;
                List<Shop_data> shop_dataList = shop_dataMapper.getDayComparison(address,dateTime,i);
                if (shop_dataList.size()==0)
                    break;
                for (Shop_data shopData:shop_dataList)
                    new_customer += shopData.getNew_customer();
                Comparison comparison = Comparison.builder().date(format.format(new java.util.Date(shop_dataList.get(0).getUpdate_time().getTime()))).number(new_customer).build();
                comparisonList.add(comparison);
            }
            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);
        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    //    ------------------------------周表

    /**
     * 获取全部的周表
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getAllWeekComparison")
    public Msg getAllWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime){
        try{
            String date;
            Integer num = 0;
            Integer walker_number = 0;
            Integer customer_number = 0;
            Integer jump_out = 0;
            Integer new_customer = 0;
            String formerDate;
            String latterDate;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> walker_numberComparisonList = new ArrayList<>();
            List<Comparison> customer_numberComparisonList = new ArrayList<>();
            List<Comparison> jump_outComparisonList = new ArrayList<>();
            List<Comparison> new_customerComparisonList = new ArrayList<>();

//            当周的数据
            List<Shop_data> weekComparisonList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:weekComparisonList) {
                walker_number += shopData.getWalker_number();
                customer_number += shopData.getCustomer_number();
                jump_out += shopData.getJump_out();
                new_customer += shopData.getNew_customer();
            }

            formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
            date = formerDate + "-" +latterDate;

            Comparison walker_numberComparison = Comparison.builder().date(date).number(walker_number).build();
            Comparison customer_numberComparison = Comparison.builder().date(date).number(customer_number).build();
            Comparison jump_outComparison = Comparison.builder().date(date).number(jump_out).build();
            Comparison new_customerComparison = Comparison.builder().date(date).number(new_customer).build();
//            加入数组
            walker_numberComparisonList.add(walker_numberComparison);
            customer_numberComparisonList.add(customer_numberComparison);
            jump_outComparisonList.add(jump_outComparison);
            new_customerComparisonList.add(new_customerComparison);

            for (int i=0;i<10;i++){
                walker_number = 0;
                customer_number = 0;
                 jump_out = 0;
                 new_customer = 0;
                weekComparisonList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (weekComparisonList.size()==0)
                    break;
                for (Shop_data shopData:weekComparisonList) {
                    walker_number += shopData.getWalker_number();
                    customer_number += shopData.getCustomer_number();
                    jump_out += shopData.getJump_out();
                    new_customer += shopData.getNew_customer();
                }
                formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
                date = formerDate + "-" +latterDate;

                 walker_numberComparison = Comparison.builder().date(date).number(walker_number).build();
                 customer_numberComparison = Comparison.builder().date(date).number(customer_number).build();
                 jump_outComparison = Comparison.builder().date(date).number(jump_out).build();
                 new_customerComparison = Comparison.builder().date(date).number(new_customer).build();
//            加入数组
                walker_numberComparisonList.add(walker_numberComparison);
                customer_numberComparisonList.add(customer_numberComparison);
                jump_outComparisonList.add(jump_outComparison);
                new_customerComparisonList.add(new_customerComparison);

                num += 7;
            }

//            倒顺序
            Collections.reverse(walker_numberComparisonList);
            Collections.reverse(customer_numberComparisonList);
            Collections.reverse(jump_outComparisonList);
            Collections.reverse(new_customerComparisonList);
//            加入map
            Map<String,List<Comparison> > comparisonMap = new HashMap<>();
            comparisonMap.put("walker_number",walker_numberComparisonList);
            comparisonMap.put("customer_number",customer_numberComparisonList);
            comparisonMap.put("jump_out",jump_outComparisonList);
            comparisonMap.put("new_number",new_customerComparisonList);

            return Msg.success(comparisonMap);


        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("服务器错误");
        }
    }


    /**
     * 人流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getWalker_numberWeekComparison")
    public Msg getWalker_numberWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer walker_number = 0;
            String formerDate;
            String latterDate;
            String date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            //当周的数据
            List<Shop_data> weekComparisonList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:weekComparisonList)
                walker_number +=shopData.getWalker_number();

            formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
            date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(date).number(walker_number).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                walker_number = 0;
                weekComparisonList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (weekComparisonList.size()==0)
                    break;
                for (Shop_data shopData:weekComparisonList)
                    walker_number +=shopData.getWalker_number();

                formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
                date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(date).number(walker_number).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 客流量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getCustomer_numberWeekComparison")
    public Msg getCustomer_numberWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer customer_number = 0;
            String formerDate;
            String latterDate;
            String date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> weekComparisonList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:weekComparisonList)
                customer_number +=shopData.getCustomer_number();

            formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
            date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(date).number(customer_number).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                customer_number = 0;
                weekComparisonList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (weekComparisonList.size()==0)
                    break;
                for (Shop_data shopData:weekComparisonList)
                    customer_number +=shopData.getCustomer_number();

                formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
                date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(date).number(customer_number).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 跳出量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getJump_outWeekComparison")
    public Msg getJump_outWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer jump_out = 0;
            String formerDate;
            String latterDate;
            String date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> weekComparisonList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:weekComparisonList)
                jump_out +=shopData.getJump_out();

            formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
            date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(date).number(jump_out).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                jump_out = 0;
                weekComparisonList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (weekComparisonList.size()==0)
                    break;
                for (Shop_data shopData:weekComparisonList)
                    jump_out +=shopData.getJump_out();

                formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
                date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(date).number(jump_out).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }

    /**
     * 新客人量
     * @param address
     * @param dateTime
     * @return
     */
    @GetMapping("/getNew_customerWeekComparison")
    public Msg getNew_customerWeekComparison(@RequestParam("address")String address,@Param("dateTime")String dateTime) {
        try{
            Integer num = 0;
            Integer new_Customer = 0;
            String formerDate;
            String latterDate;
            String date;
            DateFormat format = new SimpleDateFormat("MM.dd");
            List<Comparison> comparisonList = new ArrayList<>();

            List<Shop_data> weekComparisonList = shop_dataMapper.getThisWeekComparison(address,dateTime);
//            System.out.println(thisWeekShop_dataList);
            for (Shop_data shopData:weekComparisonList)
                new_Customer +=shopData.getNew_customer();

            formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
            latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
            date = formerDate + "-" +latterDate;

            Comparison comparison = Comparison.builder().date(date).number(new_Customer).build();
            comparisonList.add(comparison);

            for (int i=0;i<10;i++){
                new_Customer = 0;
                weekComparisonList = shop_dataMapper.getWeekComparison(address,dateTime,num);
                if (weekComparisonList.size()==0)
                    break;
                for (Shop_data shopData:weekComparisonList)
                    new_Customer +=shopData.getNew_customer();

                formerDate = format.format(new java.util.Date(weekComparisonList.get(0).getUpdate_time().getTime()));
                latterDate = format.format(new java.util.Date(weekComparisonList.get(weekComparisonList.size()-1).getUpdate_time().getTime()));
                date = formerDate + "-" +latterDate;

                comparison = Comparison.builder().date(date).number(new_Customer).build();
                comparisonList.add(comparison);

                num += 7;
            }

            Collections.reverse(comparisonList);
            return Msg.success(comparisonList);

        }catch (Exception e){
            e.printStackTrace();
            return Msg.failure().setCode(401).setMessage("返回数据失败");
        }
    }






    }
