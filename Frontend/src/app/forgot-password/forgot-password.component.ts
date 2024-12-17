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
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

    // forgot password form 
    forgotPasswordForm:any=FormGroup;
    responseMessage:any;
    
  //dependcy injection
    constructor(private formBuilder:FormBuilder,private route:Router,
      private userService:UserService,
      private snackBarServie:SnackbarService,
      private dialogRef:MatDialogRef<ForgotPasswordComponent>,
      private ngxService:NgxUiLoaderService
    ) { }



  // forgot password form
  ngOnInit(): void {
    this.forgotPasswordForm=this.formBuilder.group({
      email:[null,[Validators.required,Validators.pattern(GlobalConstant.emailRegex)]],
      
    });
  }





  // functiont to handel the submit to call api
  handleSubmit() {
    this.ngxService.start();
    const formData = this.forgotPasswordForm.value;
  
    var data = {
      email: formData.email
    };
  
    this.userService.forgotPassword(data).subscribe({
      next: (response: any) => {
        this.ngxService.stop();
        this.responseMessage = response?.message;
        this.snackBarServie.openSnackBar(this.responseMessage, '');
        this.dialogRef.close(); // Close form on success
        this.route.navigate(['/']);
      },
      error: (error) => {
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        }else{
          this.responseMessage = GlobalConstant.genericMessage;
        }
        
        this.snackBarServie.openSnackBar(this.responseMessage, GlobalConstant.error);
      }
    });
  }

}
