import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  url=environment.apiUrl;

  constructor(private httpClient:HttpClient) { }

  // method for the signup functionality
  signup(data:any){
    return this.httpClient.post(this.url+
      "/user/signup",data,{
        headers:new HttpHeaders().set('Content-Type','application/json')
      }
    )
  }


  // method for the forgot password functionality
  forgotPassword(data:any){
    return this.httpClient.post(this.url+
      "/user/forgotPassword",data,{
        headers:new HttpHeaders().set('Content-Type','application/json')
      }
    )
  }

    // method for the login functionality
    login(data:any){
      return this.httpClient.post(this.url+
        "/user/login",data,{
          headers:new HttpHeaders().set('Content-Type','application/json')
        }
      )
    }

    // method for the check token functionality
    checkToken(){
      return this.httpClient.get(this.url+
        "/user/checkToken");
    }

      // method for the forgot password functionality
    changePassword(data:any){
    return this.httpClient.post(this.url+
      "/user/changePassword",data,{
        headers:new HttpHeaders().set('Content-Type','application/json')
      }
    )
  }

}
