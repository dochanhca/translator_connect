package com.example.translateconnector.utils;

import com.imoktranslator.TranlookApplication;
import com.imoktranslator.model.LocationDataModel;
import com.imoktranslator.model.LocationModel;
import com.imoktranslator.model.ProvinceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ton on 4/2/18.
 */

public class LocationConstantCN {
    private static final String jsonData = "{\"locations\":[\n" +
            "{\"country\":\"Việt Nam\",\"provinces\":[{\"name\":\"An Giang\",\"cities\":[]},{\"name\":\"Bà Rịa - Vũng Tàu\",\"cities\":[]},{\"name\":\"Bạc Liêu\",\"cities\":[]},{\"name\":\"Bắc Kạn\",\"cities\":[]},{\"name\":\"Bắc Giang\",\"cities\":[]},{\"name\":\"Bắc Ninh\",\"cities\":[]},{\"name\":\"Bến Tre\",\"cities\":[]},{\"name\":\"Bình Dương\",\"cities\":[]},{\"name\":\"Bình Định\",\"cities\":[]},{\"name\":\"Bình Phước\",\"cities\":[]},{\"name\":\"Bình Thuận\",\"cities\":[]},{\"name\":\"Cà Mau\",\"cities\":[]},{\"name\":\"Cao Bằng\",\"cities\":[]},{\"name\":\"Cần Thơ\",\"cities\":[]},{\"name\":\"Đà Nẵng\",\"cities\":[]},{\"name\":\"Đắk Lắk\",\"cities\":[]},{\"name\":\"Đắk Nông\",\"cities\":[]},{\"name\":\"Đồng Nai\",\"cities\":[]},{\"name\":\"Đồng Tháp\",\"cities\":[]},{\"name\":\"Điện Biên\",\"cities\":[]},{\"name\":\"Gia Lai\",\"cities\":[]},{\"name\":\"Hà Giang\",\"cities\":[]},{\"name\":\"Hà Nam\",\"cities\":[]},{\"name\":\"Hà Nội\",\"cities\":[]},{\"name\":\"Hà Tĩnh\",\"cities\":[]},{\"name\":\"Hải Dương\",\"cities\":[]},{\"name\":\"Hải Phòng\",\"cities\":[]},{\"name\":\"Hòa Bình\",\"cities\":[]},{\"name\":\"Hậu Giang\",\"cities\":[]},{\"name\":\"Hưng Yên\",\"cities\":[]},{\"name\":\"Thành phố Hồ Chí Minh\",\"cities\":[]},{\"name\":\"Khánh Hòa\",\"cities\":[]},{\"name\":\"Kiên Giang\",\"cities\":[]},{\"name\":\"Kon Tum\",\"cities\":[]},{\"name\":\"Lai Châu\",\"cities\":[]},{\"name\":\"Lào Cai\",\"cities\":[]},{\"name\":\"Lạng Sơn\",\"cities\":[]},{\"name\":\"Lâm Đồng\",\"cities\":[]},{\"name\":\"Long An\",\"cities\":[]},{\"name\":\"Nam Định\",\"cities\":[]},{\"name\":\"Nghệ An\",\"cities\":[]},{\"name\":\"Ninh Bình\",\"cities\":[]},{\"name\":\"Ninh Thuận\",\"cities\":[]},{\"name\":\"Phú Thọ\",\"cities\":[]},{\"name\":\"Phú Yên\",\"cities\":[]},{\"name\":\"Quảng Bình\",\"cities\":[]},{\"name\":\"Quảng Nam\",\"cities\":[]},{\"name\":\"Quảng Ngãi\",\"cities\":[]},{\"name\":\"Quảng Ninh\",\"cities\":[]},{\"name\":\"Quảng Trị\",\"cities\":[]},{\"name\":\"Sóc Trăng\",\"cities\":[]},{\"name\":\"Sơn La\",\"cities\":[]},{\"name\":\"Tây Ninh\",\"cities\":[]},{\"name\":\"Thái Bình\",\"cities\":[]},{\"name\":\"Thái Nguyên\",\"cities\":[]},{\"name\":\"Thanh Hóa\",\"cities\":[]},{\"name\":\"Thừa Thiên - Huế\",\"cities\":[]},{\"name\":\"Tiền Giang\",\"cities\":[]},{\"name\":\"Trà Vinh\",\"cities\":[]},{\"name\":\"Tuyên Quang\",\"cities\":[]},{\"name\":\"Vĩnh Long\",\"cities\":[]},{\"name\":\"Vĩnh Phúc\",\"cities\":[]},{\"name\":\"Yên Bái\",\"cities\":[]}]},{\n" +
            "      \"country\": \"Trung Quốc\",\n" +
            "      \"provinces\": [\n" +
            "        {\n" +
            "          \"name\": \"Bắc Kinh\",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Trùng Khánh\",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Thượng Hải \",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Thiên Tân \",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hồng Kông\",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Macao\",\n" +
            "          \"cities\": []\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"An Huy\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"An Khánh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Bạng Phụ\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Bạc Châu \",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Sào Hồ\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trì Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trừ Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Phụ Dương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hợp Phì\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoài Bắc\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoài Nam\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoàng Sơn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Lục An\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Mã An Sơn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Túc Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Đồng Lăng\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Vu Hồ\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tuyên Thành\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Phúc Kiến\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Phúc Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Long Nham\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Nam Bình\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Ninh Đức\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Phủ Điền\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tuyền Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tam Minh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hạ Môn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Chương Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Cam Túc\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Bạch Ngân\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Gia Dụ Quan\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Kim Xương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tửu Tuyền\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Lan Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Bình Lương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Khánh Dương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thiên Thủy\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Vũ Uy\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trường Dịch\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Quảng Đông\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Triều Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Đông Hoản\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Phật Sơn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Quảng Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Giang Môn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Mai Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Sán Đầu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thâm Quyến\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hưng Ninh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trạm Giang\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trung Sơn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Chu Hải\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Quý Châu\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Quý Dương\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hải Nam\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Hải Khẩu\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hà Bắc\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Thạch Gia Trang\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hắc Long Giang\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"A Thành\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Cáp Nhĩ Tân\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hà Nam\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Trịnh Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hồ Bắc\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Ngạc Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoàng Cương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoàng Thạch\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Kinh Môn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Kinh Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thập Yển\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tùy Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tương Phàn\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hàm Ninh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hiếu Cảm\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Vũ Hán\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Nghi Xương\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Hồ Nam\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Trường Sa\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Giang Tô\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Nam Kinh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thường Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hoài An\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Liên Vân Cảng\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Nam Thông\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tú Thiên\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tô Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thái Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Vô Tích\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Từ Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Diêm Thành\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Dương Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trấn Giang\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Giang Tây\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Nam Xương\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Cán Châu hay Cám Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Cát Lâm\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Trường Xuân\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Cát Lâm \",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Trường Bạch\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tứ Bình\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Liêu Nguyên \",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thông Hóa \",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tùng Nguyên \",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Bạch Thành\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Liêu Ninh\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Đại Liên\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thẩm Dương\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Thanh Hải\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Tây Ninh\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Thiểm Tây\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Tây An\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Sơn Đông\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Tế Nam\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thanh Đảo\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Đông Doanh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Yên Đài\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Uy Hải\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Duy Phường\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Lâm Nghi\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Liêu Thành\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Lai Vu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Nhật Chiếu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tế Ninh\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tân Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Ca Trạch\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tri Bác\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Tảo Trang\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thái An\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Đức Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Sơn Tây\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Thái Nguyên\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Tứ Xuyên\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Thành Đô\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Vân Nam\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Côn Minh \",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Chiết Giang\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Hàng Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Hồ Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Gia Hưng\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Kim Hoa\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Lệ Thủy\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Ninh Ba\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Cù Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thiệu Hưng\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Thai Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Ôn Châu\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Chu Sơn\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Đài Loan\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Đài Bắc\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Khu tự trị dân tộc Choang Quảng Tây\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Nam Ninh\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Khu tự trị Nội Mông\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Hô Hòa Hạo Đặc\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Khu tự trị dân tộc Hồi Ninh Hạ\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Ngân Xuyên\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Khu tự trị dân tộc Duy Ngô Nhĩ Tân Cương\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"A Khắc Tô\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"A Lặc Thái\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Khách Thập\",\n" +
            "              \"address\": \"\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"Ô Lỗ Mộc Tề\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Khu tự trị Tây Tạng\",\n" +
            "          \"cities\": [\n" +
            "            {\n" +
            "              \"name\": \"Lạp Tát\",\n" +
            "              \"address\": \"\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "{\"country\":\"Anh\",\"provinces\":[]},{\"country\":\"Pháp\",\"provinces\":[]},{\"country\":\"Đức\",\"provinces\":[]},{\"country\":\"Nhật Bản\",\"provinces\":[]},{\"country\":\"Hàn Quốc\",\"provinces\":[]},{\"country\":\"Nga\",\"provinces\":[]},{\"country\":\"Thái Lan\",\"provinces\":[]},{\"country\":\"Mỹ\",\"provinces\":[]}]}\n";

    private static LocationConstantCN instance;
    private LocationDataModel locationData;


    private LocationConstantCN() {

    }

    public static LocationConstantCN getInstance() {
        if (instance == null) {
            instance = new LocationConstantCN();
        }
        return instance;
    }

    public LocationDataModel getLocationData() {
        if (locationData == null) {
            locationData = TranlookApplication.getGson().fromJson(jsonData, LocationDataModel.class);
        }
        return locationData;
    }

    public List<ProvinceModel> getAllProvinceIn(String selectedCountry) {
        List<ProvinceModel> provinceList = new ArrayList<>();

        for (LocationModel location : getLocationData().getLocations()) {
            if (location.getCountry().equals(selectedCountry)) {
                provinceList = location.getProvinces();
                return provinceList;
            }
        }

        return provinceList;
    }

}
