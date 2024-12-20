import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  url = environment.apiUrl;
  constructor(private httpClient: HttpClient) { }


  // method for the add category functionality
  add(data: any) {
    return this.httpClient.post(this.url +
      "/category/add", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }

  // method for the update category functionality
  update(data: any) {
    return this.httpClient.post(this.url +
      "/category/updateCategory", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }


  // function to get all details of dashboar
  getCategorys() {
    return this.httpClient.get(this.url + "/category/get");
  }

  // function to get all details of dashboar
  getFilteredCategorys() {
    return this.httpClient.get(this.url + "/category/get?filterValue=true");
  }


}
