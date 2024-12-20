import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BillService {

  url = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }



  // method to genereate reports 
  generateReport(data: any) {
    return this.httpClient.post(this.url +
      "/bill/generateReport", data, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }

  // function to get pdf
  getPdf(data: any): Observable<Blob> {
    return this.httpClient.post(this.url + "/bill/getPdf", data, { responseType: 'blob' });
  }

  // function to get bills
  getBills() {
    return this.httpClient.get(this.url + "/bill/getBills");
  }


  // method to delete bill  
  delete(id: any) {
    return this.httpClient.post(this.url +
      "/bill/delete/"+id, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    }
    )
  }
}
