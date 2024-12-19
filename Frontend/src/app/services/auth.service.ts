import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private router:Router) { }

  // function to authenticate
  public isAuthenticated():boolean{
    const token=localStorage.getItem('token')
    if (!token) {
      this.router.navigate(['/']);
      return false;
    }
    else{
      return true;
    }
  }
}
