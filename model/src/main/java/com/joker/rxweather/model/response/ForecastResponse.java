package com.joker.rxweather.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Joker on 2015/10/31.
 */
public class ForecastResponse {

  /*
    {
      HeWeather data service 3.0":
      [
        {
          "aqi":
          {
            "city":
            {
              "aqi": "60",
              "co": "1",
              "no2": "58",
              "o3": "37",
              "pm10": "67",
              "pm25": "34",
              "qlty": "良",
              "so2": "21"
            }
          },
          "basic":
          {
            "city": "唐山",
            "cnty": "中国",
            "id": "CN101090501",
            "lat": "39.616000",
            "lon": "118.180000",
            "update":
            {
              "loc": "2015-10-31 18:49",
              "utc": "2015-10-31 10:49"
            }
          },
          "daily_forecast":
          [
            {
              "astro":
              {
                "sr": "06:34",
                "ss": "17:07"
              },
              "cond":
              {
                "code_d": "100",
                "code_n": "101",
                "txt_d": "晴",
                "txt_n": "多云"
              },
              "date": "2015-10-31",
              "hum": "19",
              "pcpn": "0.0",
              "pop": "0",
              "pres": "1031",
              "tmp":
              {
                "max": "17",
                "min": "2"
              },
              "vis": "10",
              "wind":
              {
                "deg": "275",
                "dir": "西风",
                "sc": "微风",
                "spd": "11"
              }
            }
          ],
          "hourly_forecast":
          [
            {
              "date": "2015-10-31 22:00",
              "hum": "32",
              "pop": "0",
              "pres": "1029",
              "tmp": "9",
              "wind":
              {
                "deg": "258",
                "dir": "西南风",
                "sc": "微风",
                "spd": "10"
              }
            }
          ],
          "now":
          {
            "cond":
            {
              "code": "100",
              "txt": "晴"
            },
            "fl": "11",
            "hum": "55",
            "pcpn": "0",
            "pres": "1029",
            "tmp": "8",
            "vis": "10",
            "wind":
            {
              "deg": "200",
              "dir": "南风",
              "sc": "4-5",
              "spd": "11"
            }
          },
          "status": "ok",
          "lifeSuggestion":
          {
            "comf":
            {
              "brf": "舒适",
              "txt": "白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"
            },
            "cw":
            {
              "brf": "较适宜",
              "txt": "较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"
            },
            "drsg":
            {
              "brf": "较冷",
              "txt": "建议着大衣、呢外套加毛衣、卫衣等服装。体弱者宜着厚外套、厚毛衣。因昼夜温差较大，注意增减衣服。"
            },
            "flu":
            {
              "brf": "极易发",
              "txt": "天气寒冷，且昼夜温差很大，极易发生感冒。请特别注意增加衣服保暖防寒。"
            },
            "sport":
            {
              "brf": "适宜",
              "txt": "天气较好，赶快投身大自然参与户外运动，尽情感受运动的快乐吧。"
            },
            "trav":
            {
              "brf": "适宜",
              "txt": "天气较好，但丝毫不会影响您出行的心情。温度适宜又有微风相伴，适宜旅游。"
            },
            "uv":
            {
              "brf": "弱",
              "txt": "紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"
            }
          }
        }
      ]
    }*/

  @Expose @SerializedName("HeWeather data service 3.0") private List<Main> mains;

  public List<Main> getMains() {
    return mains;
  }

  public class Main extends BaseResponse {

    /**
     * 基本信息
     */
    @Expose private Basic basic;

    public Basic getBasic() {
      return basic;
    }

    public class Basic {

      @Expose @SerializedName("id") public String cityId;
      @Expose @SerializedName("city") public String cityName;

      @Override public String toString() {
        return "Basic{" +
            "cityName='" + cityName + '\'' +
            ", cityId='" + cityId + '\'' +
            '}';
      }
    }

    /**
     * 一周气温
     */
    @Expose @SerializedName("daily_forecast") private List<Forecast> forecasts;

    public List<Forecast> getForecasts() {
      return forecasts;
    }

    public class Forecast {

      @Expose @SerializedName("cond") private Condition condition;

      public Condition getCondition() {
        return condition;
      }

      public class Condition {

        @Expose @SerializedName("code_d") public String dayCode;
        @Expose @SerializedName("code_n") public String nightCode;
        @Expose @SerializedName("txt_d") public String dayText;
        @Expose @SerializedName("txt_n") public String nightText;

        @Override public String toString() {
          return "Cond{" +
              "dayCode='" + dayCode + '\'' +
              ", nightCode='" + nightCode + '\'' +
              ", dayText='" + dayText + '\'' +
              ", nightText='" + nightText + '\'' +
              '}';
        }
      }

      @Expose public String date;

      @Expose @SerializedName("tmp") private Temperature temperature;

      public Temperature getTemperature() {
        return temperature;
      }

      public class Temperature {

        @Expose @SerializedName("max") public String maxTemp;
        @Expose @SerializedName("min") public String minTemp;

        @Override public String toString() {
          return "Temperature{" +
              "maxTemp='" + maxTemp + '\'' +
              ", minTemp='" + minTemp + '\'' +
              '}';
        }
      }

      @Expose @SerializedName("wind") private Wind wind;

      public Wind getWind() {
        return wind;
      }

      public class Wind {
        @Expose @SerializedName("dir") public String description;
        @Expose @SerializedName("sc") public String windGrade;

        @Override public String toString() {
          return "Wind{" +
              "description='" + description + '\'' +
              ", windGrade='" + windGrade + '\'' +
              '}';
        }
      }

      @Override public String toString() {
        return "Forecast{" +
            "condition=" + condition +
            ", date='" + date + '\'' +
            ", temperature=" + temperature +
            ", wind=" + wind +
            '}';
      }
    }

    /**
     * 当前气温
     */
    @Expose @SerializedName("now") private CurrentWeather currentWeather;

    public CurrentWeather getCurrentWeather() {
      return currentWeather;
    }

    public class CurrentWeather {

      @Expose @SerializedName("cond") private Condition condition;

      public Condition getCondition() {
        return condition;
      }

      public class Condition {

        @Expose @SerializedName("code") public String weatherCode;
        @Expose @SerializedName("txt") public String weatherText;

        @Override public String toString() {
          return "Condition{" +
              "weatherCode='" + weatherCode + '\'' +
              ", weatherText='" + weatherText + '\'' +
              '}';
        }
      }

      @Expose @SerializedName("tmp") public String temperature;

      @Expose @SerializedName("wind") private Wind wind;

      public Wind getWind() {
        return wind;
      }

      public class Wind {
        @Expose @SerializedName("dir") public String description;
        @Expose @SerializedName("sc") public String windGrade;

        @Override public String toString() {
          return "Wind{" +
              "description='" + description + '\'' +
              ", windGrade='" + windGrade + '\'' +
              '}';
        }
      }

      @Override public String toString() {
        return "CurrentWeather{" +
            "condition=" + condition +
            ", temperature='" + temperature + '\'' +
            ", wind=" + wind +
            '}';
      }
    }

    /**
     * 生活指数
     */
    @Expose @SerializedName("suggestion") private Suggestion suggestion;

    public Suggestion getSuggestion() {
      return suggestion;
    }

    public class Suggestion {

      @Expose @SerializedName("drsg") private Drsg drsg;

      public Drsg getDrsg() {
        return drsg;
      }

      public class Drsg {

        @Expose @SerializedName("txt") public String description;

        @Override public String toString() {
          return "Drsg{" +
              "description='" + description + '\'' +
              '}';
        }
      }

      @Override public String toString() {
        return "Suggestion{" +
            "drsg=" + drsg +
            '}';
      }
    }

    @Override public String toString() {
      return "Main{" +
          "basic=" + basic +
          ", forecasts=" + forecasts +
          ", currentWeather=" + currentWeather +
          ", suggestion=" + suggestion +
          '}';
    }
  }

  @Override public String toString() {
    return "ForecastResponse{" +
        "mains=" + mains +
        '}';
  }
}
