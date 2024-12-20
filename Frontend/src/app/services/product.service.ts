import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  url = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }


  // method for the add product functionality
  add(data: any) {
    return this.httpClient.post(this.url +
      "/product/add", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }

  // method for the add update functionality
  update(data: any) {
    return this.httpClient.post(this.url +
      "/product/update", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }

  // function to get all details of products
  getProducts() {
    return this.httpClient.get(this.url + "/product/get");
  }

  // method for the add update status of product functionality
  updateStatus(data: any) {
    return this.httpClient.post(this.url +
      "/product/updateStatus", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }

  // function to delete the product
  delete(id: any) {
    return this.httpClient.post(this.url +
      "/product/delete/" + id, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }


  // function to get all details of products by category
  getProductByCategory(id:any) {
    return this.httpClient.get(this.url + "/product/getByCategory/"+id);
  }

   // function to get all details of products by id
   getProductById(id:any) {
    return this.httpClient.get(this.url + "/product/getById/"+id);
  }


  
}
