import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { SnackbarService } from '../services/snackbar.service';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { GlobalConstant } from '../shared/global-constants';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  // variable to validata the password
  password=true;
  confirmPassword=true;

  // signup form 
  signupForm:any=FormGroup;
  responseMessage:any;


  //dependcy injection
  constructor(private formBuilder:FormBuilder,private route:Router,
    private userService:UserService,
    private snackBarServie:SnackbarService,
    private dialogRef:MatDialogRef<SignupComponent>,
    private ngxService:NgxUiLoaderService
  ) { }


  // signup form
  ngOnInit(): void {
    this.signupForm=this.formBuilder.group({
      name:[null,[Validators.required,Validators.pattern(GlobalConstant.nameRegex)]],
      email:[null,[Validators.required,Validators.pattern(GlobalConstant.emailRegex)]],
      contactNumber:[null,[Validators.required,Validators.pattern(GlobalConstant.contactNumberRegex)]],
      password:[null,[Validators.required]],
      confirmPassword:[null,[Validators.required]]
    });
  }


  // function to validate the submitted passwords
  validateSubmit(){
    if (this.signupForm.controls['password'].value !=this.signupForm.controls['confirmPassword'].value) {
      return true;
    }
    else{
      return false;
    }
  }

  // functiont to handel the submit to call api
  handleSubmit(){
    this.ngxService.start();
    var formData=this.signupForm.value;
    var data={
      name:formData.name,
      email:formData.email,
      contactNumber:formData.contactNumber,
      password:formData.password
    }

    this.userService.signup(data).subscribe((response:any)=>{
      this.ngxService.stop();
      this.dialogRef.close();
      this.responseMessage=response?.message;
      this.snackBarServie.openSnackBar(this.responseMessage,"");
      this.route.navigate(['/']);
    },(error)=>{
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage=error.error?.message;
      }
      else{
        this.responseMessage=GlobalConstant.genericMessage;
      }
      this.snackBarServie.openSnackBar(this.responseMessage,GlobalConstant.error);
    })
  }

}
