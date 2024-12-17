import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from '../services/snackbar.service';
import { UserService } from '../services/user.service';
import { SignupComponent } from '../signup/signup.component';
import { GlobalConstant } from '../shared/global-constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    // Login form 
    hide=true;
    loginForm:any=FormGroup;
    responseMessage:any;
  

  //dependcy injection
  constructor(private formBuilder:FormBuilder,private route:Router,
    private userService:UserService,
    private snackBarServie:SnackbarService,
    private dialogRef:MatDialogRef<LoginComponent>,
    private ngxService:NgxUiLoaderService,
    private router:Router
  ) { }

  // login form
  ngOnInit(): void {
    this.loginForm=this.formBuilder.group({
      email:[null,[Validators.required,Validators.pattern(GlobalConstant.emailRegex)]],
      password:[null,[Validators.required]]
      
    });
  }


   // functiont to handel the submit to call api
   handleSubmit() {
    this.ngxService.start();
    const formData = this.loginForm.value;
  
    const data = {
      email: formData.email,
      password: formData.password
    };
  
    this.userService.login(data).subscribe({
      next: (response: any) => {
        this.ngxService.stop();
        this.dialogRef.close(); // Close form on success
        localStorage.setItem('token',response.token)
        this.route.navigate(['/cafe/dashboard']);
        
      },
      error: (error) => {
        this.ngxService.stop();
       if (error.error?.message) {
        this.responseMessage = error.error?.message ;
       }
        else{
          this.responseMessage = GlobalConstant.genericMessage ;
        }
        this.snackBarServie.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    });
  }

}
