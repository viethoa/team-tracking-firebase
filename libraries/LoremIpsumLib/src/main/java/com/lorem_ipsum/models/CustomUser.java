package com.lorem_ipsum.models;

import java.util.Date;

/**
 * Created by Originally.US on 11/12/2014.
 */
public class CustomUser {

    // End_user
    //    "id": 29,
    //    "first_name": "Wei Kiat",
    //    "last_name": "Tay",
    //    "phone_number": 123,
    //    "created_datetime": 1418013222,
    //    "modified_datetime": 1418013222,
    //    "display_name": "Wei Kiat Tay"

    // Supplier
    //    "id": 17,
    //    "created_datetime": 1410278400,
    //    "modified_datetime": 1411216858,
    //    "company_name": "Originally US International",
    //    "phone_number": 81282904,
    //    "registration_number": 3412342142,
    //    "address_line1": "Maxwell Road 123",
    //    "address_line2": "Urban",
    //    "postal_code": 670437,
    //    "country": "Singapore",
    //    "gst_registered": 0,
    //    "credit_balance": 370,
    //    "business_hours": "7pm to 12am",
    //    "company_photo_url": null,
    //    "company_website_url": "http:\/\/originally.us",
    //    "description": "Let’s cut out the middle man. No more Consultants, Sales and Project Managers who don’t know what they are talking about.\ \ \ \ Imagine working with a company where their entire team, including Consultants, Project Managers and Designers, are also programmers who will work on your project.\ \ \ \ This is Originally.US.\ \ \ \ We are based in the sunny island of Singapore!",
    //    "short_description": "No bullshit software development!"

    public int id;
    public String first_name;
    public String last_name;
    public int phone_number;
    public Date created_datetime;
    public Date modified_datetime;
    public String display_name;

    public String company_name;
    public int registration_number;
    public String address_line1;
    public String address_line2;
    public int postal_code;
    public String country;
    public int gst_registered;
    public int credit_balance;
    public boolean company_photo_url;
    public String company_website_url;
    public String description;
    public String short_description;

}
