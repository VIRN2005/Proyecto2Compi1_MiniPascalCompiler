; ModuleID = 'MiniPascal'
source_filename = "C:\Users\Victo\OneDrive\Documents\- UNITEC\Ing. Sistemas\2025 - Periodo 1\Compiladores 1\Proyect 2\Proyecto2Compi1_MiniPascalCompiler\PascalCompiler\Programs\Salary.txt"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; ModuleID = 'MiniPascal'
source_filename = "output.ll"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str._t1a2637cf_63e4_4d25_87b7_80ac6261f724 = private unnamed_addr constant [14 x i8] c"Sueldo Total:\00"

@.str._t39d24769_48a1_4853_b6a1_c1b16a79cf7f = private unnamed_addr constant [20 x i8] c"Buen sueldo semanal\00"

%struct._IO_FILE = type { i8*, i32, i32, i32, i8*, i8*, i8*, i8*, i8*, i32, i32, i32, i32, i8*, i8*, i8*, i32, i32, i32 }
@str_fmt = unnamed_addr constant [4 x i8] c"%d\0A\00"
@stdin = external global %struct._IO_FILE*
@double_fmt = private unnamed_addr constant [4 x i8] c"%f\0A\00"

define i32 @main() {
br label %CalculoSueldo
CalculoSueldo:
%sueldoHora = alloca i32
store i32 0, i32* %sueldoHora
%horas = alloca i32
store i32 0, i32* %horas
%sueldoTotal = alloca i32
store i32 0, i32* %sueldoTotal
%_t0 = alloca i32
store i32 150, i32* %_t0
store i32 150, i32* %sueldoHora
%_t1 = alloca i32
store i32 40, i32* %_t1
store i32 40, i32* %horas
%_t2 = alloca i32
store i32 40, i32* %_t2
%_t3 = alloca i32
store i32 150, i32* %_t3
%horas_val = load i32, i32* %horas
%sueldoHora_val = load i32, i32* %sueldoHora
%_t4_val = mul i32 %horas_val, %sueldoHora_val
store i32 %_t4_val, i32* %sueldoTotal
call void @write_string(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.str._t1a2637cf_63e4_4d25_87b7_80ac6261f724, i32 0, i32 0))

%_t6 = alloca i32
store i32 %_t4_val, i32* %_t6
call void @write_int(i32 %_t4_val)

%_t7 = alloca i32
store i32 %_t4_val, i32* %_t7
%_t8 = alloca i32
store i32 5000, i32* %_t8
%_tebb5cdaa_086c_4f45_aa0e_9aa5a4453f43 = add i32 5000, 0
%_t9 = icmp sgt i32 %_t4_val, %_tebb5cdaa_086c_4f45_aa0e_9aa5a4453f43
br i1 %_t9, label %L_t9, label %Label0
L_t9:
call void @write_string(i8* getelementptr inbounds ([20 x i8], [20 x i8]* @.str._t39d24769_48a1_4853_b6a1_c1b16a79cf7f, i32 0, i32 0))

br label %Label1
Label0:
br label %Label1
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

; Function Attrs: noinline nounwind optnone uwtable
declare i32 @printf(i8*, ...) #0

attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 10.0.0-4ubuntu1 "}
