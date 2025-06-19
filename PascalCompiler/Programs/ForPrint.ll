; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._tab840d9a_f9bf_48e3_806f_1ae4a7a3bb57 = private unnamed_addr constant [4 x i8] c"j: \00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %ForPrint
ForPrint:
%j = alloca i32
store i32 0, i32* %j
%_t0 = alloca i32
store i32 0, i32* %_t0
store i32 0, i32* %j
%_t1 = alloca i32
store i32 1, i32* %_t1
store i32 1, i32* %j
br label %Label0
Label0:
%_t2 = alloca i32
store i32 15, i32* %_t2
%_t1_val = load i32, i32* %_t1
%_tb1320530_c181_458d_aae6_5c4f14a3c555 = add i32 15, 0
%_t3 = icmp sle i32 %_t1_val, %_tb1320530_c181_458d_aae6_5c4f14a3c555
br i1 %_t3, label %L_t3, label %Label1
L_t3:
call void @write_string(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str._tab840d9a_f9bf_48e3_806f_1ae4a7a3bb57, i32 0, i32 0))

%_t5 = alloca i32
store i32 1, i32* %_t5
%_tca587581_f5f4_4827_843a_34d83720f29d = load i32, i32* %j
call void @write_int(i32 %_tca587581_f5f4_4827_843a_34d83720f29d)

%loopVar_val = add i32 %_t1_val, 1
store i32 %loopVar_val, i32* %j
store i32 %loopVar_val, i32* %_t1
br label %Label0
Label1:
ret i32 0
}
define void @write_double(double %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @double_fmt, i32 0, i32 0), double %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_int(i32 %num) {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  call i32 (i8*, i8*, ...) @sprintf(i8* %buf_ptr, i8* getelementptr inbounds ([4 x i8], [4 x i8]* @str_fmt, i32 0, i32 0), i32 %num)
  call i32 @puts(i8* %buf_ptr)
  ret void
}
define void @write_string(i8* %str) {
  %str_ptr = alloca i8*
  store i8* %str, i8** %str_ptr
  %str_val = load i8*, i8** %str_ptr
  call i32 @puts(i8* %str_val)
  ret void
}
define i32 @read() {
  %buf = alloca [32 x i8], align 1
  %buf_ptr = getelementptr inbounds [32 x i8], [32 x i8]* %buf, i32 0, i32 0
  %stdin_val = load %struct._IO_FILE*, %struct._IO_FILE** @stdin
  %result = call i8* @fgets(i8* %buf_ptr, i32 32, %struct._IO_FILE* %stdin_val)
  %num = call i32 @atoi(i8* %buf_ptr)
  ret i32 %num
}
declare i32 @atoi(i8*)
declare i32 @sprintf(i8*, i8*, ...)
declare i32 @puts(i8*)
declare i8* @fgets(i8*, i32, %struct._IO_FILE*)
declare void @exit(i32)
